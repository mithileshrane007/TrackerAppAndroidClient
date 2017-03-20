package com.example.infiny.mylocationtracker.Activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.infiny.mylocationtracker.ConfigApp.Config;
import com.example.infiny.mylocationtracker.Helpers.GPSTracker;
import com.example.infiny.mylocationtracker.Helpers.SessionManager;
import com.example.infiny.mylocationtracker.IDataFetch;
import com.example.infiny.mylocationtracker.Listeners.CheckListener;
import com.example.infiny.mylocationtracker.Listeners.StopServiceListener;
import com.example.infiny.mylocationtracker.NetworkUtils.VolleyUtils;
import com.example.infiny.mylocationtracker.R;
import com.example.infiny.mylocationtracker.Utils.ProgressWheel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewLocation extends AppCompatActivity implements View.OnClickListener,IDataFetch{
    AlarmManager alarm;

    private Context mContext;
    IDataFetch iDataFetch;

    PendingIntent pintent;
    MenuItem add_new;
    SessionManager sessionManager;

    Timer timer;
    TimerTask timerTask;
    long starttime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedtime = 0L;
    int t = 1;
    int secs = 0;
    int mins = 0;
    int hours=0;
    int milliseconds = 0;
    Handler handler = new Handler();
    ExecutorService executor = Executors.newFixedThreadPool(1);

    TextView tv_name,tv_email,tv_start_stop,tv_checkout;
    ProgressWheel progressBarHour,progressBarMinutes;
    ImageView iv_start_stop,iv_checkout;
    CircleImageView iv_profilePic;


    boolean running;
    int progress=0;
    Bundle bundle;
    private DisplayImageOptions options;
    private AlarmManager alarmMgr;
    private PendingIntent pendingI;
    private StopServiceListener stopServiceListener;
    private CheckListener checkListener;
    TextInputLayout edt_despcrition;
    VolleyUtils volleyUtils;
    Button btnSubmit;
    MapView mapView;
    private GoogleMap mMap;
    private ScrollView hsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_new_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Location Tracker");
        getSupportActionBar().hide();
        mContext=this;
        volleyUtils=new VolleyUtils();
        sessionManager=new SessionManager(mContext);
        Log.d("val1","in onCreate");
        if (getIntent()!=null)
            bundle=getIntent().getExtras();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.profileplaceholder)
                .showImageForEmptyUri(R.drawable.profileplaceholder)
                .showImageOnFail(R.drawable.profileplaceholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();


        setUpUi2();
        iDataFetch=this;

        iv_start_stop.setOnClickListener(this);
        iv_checkout.setOnClickListener(this);

        if (bundle!=null) {
            tv_email.setText(bundle.getString("email"));
            tv_name.setText(bundle.getString("first_name")+" "+bundle.getString("last_name"));
            ImageLoader.getInstance().displayImage(Config.IMG_URL+ bundle.getString("image"), iv_profilePic , options);

            sessionManager.setUser(tv_name.getText().toString(),tv_email.getText().toString(),Config.IMG_URL+ bundle.getString("image"));
        }else {
            tv_email.setText( sessionManager.getUser().get(1));
            tv_name.setText(sessionManager.getUser().get(0));
            ImageLoader.getInstance().displayImage(sessionManager.getUser().get(2), iv_profilePic , options);
        }


    }

    private void setUpUi2() {
        progressBarMinutes = (ProgressWheel) findViewById(R.id.progressBarMinutes);
        progressBarHour = (ProgressWheel) findViewById(R.id.progressBarHour);
        iv_profilePic= (CircleImageView) findViewById(R.id.iv_profilePic);
        iv_start_stop= (ImageView) findViewById(R.id.iv_start_stop);
        iv_checkout= (ImageView) findViewById(R.id.iv_checkout);
        tv_name= (TextView) findViewById(R.id.tv_name);
        tv_start_stop= (TextView) findViewById(R.id.tv_start_stop);
        tv_email= (TextView) findViewById(R.id.tv_email);
        tv_checkout= (TextView) findViewById(R.id.tv_checkout);

        if (sessionManager.getClicked()) {
            iv_start_stop.setImageResource(R.drawable.ic_stop_white_48dp);
            tv_start_stop.setText("Stop");

        }
        else {
            iv_start_stop.setImageResource(R.drawable.ic_play_circle_filled_white_48dp);
            tv_start_stop.setText("Start");
            progressBarHour.setText(0+"h");

            progressBarMinutes.setText(0+"m");
        }


//        Calendar time = Calendar.getInstance();
//        String time_out=sessionManager.getTrackTimeOut();
//        ;
//        if (time_out.contains("."))
//        {
//            String [] time_out_arr=time_out.split("\\.");
//            time.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time_out_arr[0]));
//            time.set(Calendar.MINUTE, Integer.valueOf(time_out_arr[1]));
//
//        }else {
//            time.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time_out));
//
//        }
////        time.set(Calendar.HOUR_OF_DAY, 17);
////        time.set(Calendar.MINUTE, 30);

        checkListener=new CheckListener();
        stopServiceListener  = new StopServiceListener();
        registerReceiver(stopServiceListener,new IntentFilter("CANCEL_SENDING"));
//        registerReceiver(checkListener,new IntentFilter("CHECK_TRACK"));

//
//        Intent intentCancel=new Intent();
//        intentCancel.setAction("CANCEL_SENDING");
//        pendingI = PendingIntent.getBroadcast(getApplicationContext(),1, intentCancel, 0);
//        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmMgr.setRepeating(AlarmManager.RTC, time.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingI);

//        Intent intentCheckTrack=new Intent();
//        intentCheckTrack.setAction("CHECK_TRACK");
//        pendingI = PendingIntent.getBroadcast(getApplicationContext(),2, intentCheckTrack, 0);
//        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        long aDouble=(Long.parseLong(sessionManager.getTrackTimeOut())*3600*1000);
//        Calendar cur_cal = Calendar.getInstance();
//        cur_cal.setTimeInMillis(System.currentTimeMillis());
//        cur_cal.add(Calendar.SECOND, 2);
//        alarmMgr.setRepeating(AlarmManager.RTC,cur_cal.getTimeInMillis(),aDouble, pendingI);

    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.d("val1","in onResume");
        Intent intentCheckTrack=new Intent();
        intentCheckTrack.setAction("CHECK_TRACK");
        pendingI = PendingIntent.getBroadcast(getApplicationContext(),2, intentCheckTrack, 0);
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long aDouble=(Long.parseLong(sessionManager.getTrackTimeOut())*3600*1000);
        Calendar cur_cal = Calendar.getInstance();
        cur_cal.setTimeInMillis(System.currentTimeMillis());
        cur_cal.add(Calendar.SECOND, 2);
        alarmMgr.setRepeating(AlarmManager.RTC,cur_cal.getTimeInMillis(),aDouble, pendingI);


        Log.d("val1","in sessionManager.getClicked()"+sessionManager.getClicked());

        if (sessionManager.getClicked()) {
            iv_start_stop.setImageResource(R.drawable.ic_stop_white_48dp);
            tv_start_stop.setText("Stop");
            startTimer();

        }
        else {

            iv_start_stop.setImageResource(R.drawable.ic_play_circle_filled_white_48dp);
            tv_start_stop.setText("Start");

        }
    }


    public void checkAlarm()
    {
        Intent intent = new Intent(getApplicationContext(), MyIntentLocationService.class);

        boolean alarmUp = (PendingIntent.getService(mContext, 0,
                intent,
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp)
        {
            Log.d("val", "Alarm is already active");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location_act_add, menu);
        add_new= menu.findItem(R.id.add_new);
        add_new.setEnabled(true);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add_new:
                Toast.makeText(mContext,"Clciked add",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }


    @Override
    public void onClick(View view) {



        switch (view.getId())
        {
            case R.id.iv_start_stop:

                if (!sessionManager.getClicked()) {

                    try {
                        starttime = Calendar.getInstance().getTime().getTime();
                        sessionManager.setTimerStartTime(starttime);
                        sessionManager.setClicked(true);

                        //timer will start
                        startTimer();

                        volleyUtils.setOnlineStatus(true, sessionManager.getAuthToken(), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = new Date();
                        SimpleDateFormat sdf1=new SimpleDateFormat("HH:mm");
                        sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));

                        String time=sdf.format(date);
                        String min=sdf1.format(date);


                        volleyUtils.setLoggedHours(sessionManager.getId(), sdf.format(date), sdf1.format(date), true, false, sessionManager.getAuthToken(), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject=new JSONObject(response);

                                    if (!jsonObject.getBoolean("error")) {
//                                        if (LogCheck.count(LogCheck.class)<=0){
//                                            LogCheck logCheck=new LogCheck();
//                                            logCheck.setPrev_t(jsonObject.getString("prev_time"));
//                                            logCheck.setLog_hor(jsonObject.getString("loggedhour"));
//                                            logCheck.setDate(jsonObject.getString("date"));
//                                            logCheck.save();
//                                        }else {
//                                            LogCheck logCheck=LogCheck.findById(LogCheck.class,1);
////                                            DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd HH:mm");
////                                            TimeZone tz = TimeZone.getDefault();
////
////                                            formatter.setTimeZone(tz);
////                                            Date date = formatter.parse(logCheck.getDate()+" "+logCheck.getPrev_t());
//                                            Date dateCalss= DateUtils.getDate(logCheck.getDate()+" "+logCheck.getPrev_t());
//                                            long dateclassmill=dateCalss.getTime();
//
////                                            Calendar calendar = Calendar.getInstance();
////                                            calendar.setTime(date);
////                                            System.out.println("TIme"+ calendar.getTimeInMillis());
//
//
//                                            long new_time_millis=sessionManager.getTimerStartTime();
//                                            long diff=new_time_millis-dateclassmill;
//                                            long check=Long.parseLong(sessionManager.getTrackTimeOut())*3600*1000;
//
//
//                                            Log.d("Diff check","Start diff "+diff+" check "+check);
//                                            if (diff >=Long.parseLong(sessionManager.getTrackTimeOut())*3600*1000){
//                                                Intent intentCancel=new Intent();
//                                                Calendar cur_cal = Calendar.getInstance();
//                                                cur_cal.setTimeInMillis(System.currentTimeMillis());
//                                                cur_cal.add(Calendar.SECOND, 2);
//                                                intentCancel.setAction("CANCEL_SENDING");
//                                                pendingI = PendingIntent.getBroadcast(getApplicationContext(),1, intentCancel, 0);
//                                                alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                                                alarmMgr.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingI);
//                                            }else {
//                                                LogCheck logCheck1 = LogCheck.findById(LogCheck.class, 1);
//                                                logCheck1.setLog_hor(jsonObject.getString("loggedhour"));
//                                                logCheck1.setPrev_t(jsonObject.getString("prev_time"));
//                                                logCheck1.setDate(jsonObject.getString("date"));
//
//                                                logCheck1.save();
//                                            }
//
//                                        }
//
//                                        if (Long.parseLong(jsonObject.getString("loggedhour"))>= Long.parseLong(sessionManager.getTrackTimeOut())*3600*1000){
//                                            Intent intentCancel=new Intent();
//                                            Calendar cur_cal = Calendar.getInstance();
//                                            cur_cal.setTimeInMillis(System.currentTimeMillis());
//                                            cur_cal.add(Calendar.SECOND, 2);
//                                            intentCancel.setAction("CANCEL_SENDING");
//                                            pendingI = PendingIntent.getBroadcast(getApplicationContext(),1, intentCancel, 0);
//                                            alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                                            alarmMgr.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingI);
//                                        }
                                    } else {

                                        Toast.makeText(mContext,"Else setLoggedHours",Toast.LENGTH_SHORT).show();
                                    }

                                    if (sessionManager.getLoggedHours()<=Long.parseLong(sessionManager.getTrackTimeOut())*3600*1000){

                                    }else {

                                        sessionManager.setClicked(false);
                                        stoptimertask(iv_start_stop);
                                        iv_start_stop.setImageResource(R.drawable.ic_play_circle_filled_white_48dp);
                                        tv_start_stop.setText("Start");
                                        progressBarMinutes.stopSpinning();
                                        sessionManager.clearTimer();

                                        Intent intentCancel=new Intent();
                                        Calendar cur_cal = Calendar.getInstance();
                                        cur_cal.setTimeInMillis(System.currentTimeMillis());
                                        cur_cal.add(Calendar.SECOND, 2);
                                        intentCancel.setAction("CANCEL_SENDING");
                                        PendingIntent pendingI = PendingIntent.getBroadcast(getApplicationContext(),1, intentCancel, 0);
                                        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                        alarmMgr.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), 1, pendingI);

                                    }


                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        iv_start_stop.setImageResource(R.drawable.ic_stop_white_48dp);
                        tv_start_stop.setText("Stop");
                        progressBarMinutes.spin();
                        progressBarMinutes.setSpinSpeed(1);


                        Log.d("val", "in time star::" + starttime);
                        Calendar cur_cal = Calendar.getInstance();
                        cur_cal.setTimeInMillis(System.currentTimeMillis());
                        cur_cal.add(Calendar.SECOND, 2);
                        Log.d("val", "Calender Set time:" + cur_cal.getTime());
                        Intent intent = new Intent(getApplicationContext(), MyIntentLocationService.class);
                        pintent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
                        alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        long interval =Long.valueOf(sessionManager.getTrackTimeInterval());
                        interval=interval*60000;
                        alarm.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), interval, pintent);

//                        alarm.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), 30000, pintent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else{


                    try {
                        sessionManager.setClicked(false);
                        stoptimertask(iv_start_stop);
                        //timer will stop


                        long millis=Calendar.getInstance().getTime().getTime()-sessionManager.getTimerStartTime();
                        Log.d("val","in time ed::"+millis+"\n fdffd:"+String.format("%s h, %s m",
                                String.valueOf(TimeUnit.MILLISECONDS.toHours(millis)),
                                String.valueOf(TimeUnit.MILLISECONDS.toMinutes(millis))));




                        long updated = timeSwapBuff + millis;
                        int minutes = (int) ((updated / (1000*60)) % 60);
                        int updatedhours   = (int) ((updated / (1000*60*60)) % 24);
                        progressBarHour.setText(updatedhours+"h");
                        progressBarMinutes.setText(minutes +"m");
                        Intent intent = new Intent(getBaseContext(), MyIntentLocationService.class);
                        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
                        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);
                        pendingIntent.cancel();
                        iv_start_stop.setImageResource(R.drawable.ic_play_circle_filled_white_48dp);
                        tv_start_stop.setText("Start");
                        progressBarMinutes.stopSpinning();

                        long smallDiff=millis-sessionManager.getLoggedHoursTemp();
                        sessionManager.setLoggedHours(sessionManager.getLoggedHours()+smallDiff+sessionManager.getLoggedHoursTemp());

//                        sessionManager.setLoggedHours(sessionManager.getLoggedHours()+millis);
                        sessionManager.clearTimer();

                        volleyUtils.setOnlineStatus(false, sessionManager.getAuthToken(), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = new Date();
                        SimpleDateFormat sdf1=new SimpleDateFormat("HH:mm");
                        sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));

                        String time=sdf.format(date);
                        String min=sdf1.format(date);


                        volleyUtils.setLoggedHours(sessionManager.getId(), sdf.format(date), sdf1.format(date), false, true, sessionManager.getAuthToken(), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {


                                try {
                                    JSONObject jsonObject=new JSONObject(response);
//                                    if (Integer.parseInt(jsonObject.getString("loggedhour"))>=Integer.parseInt(sessionManager.getTrackTimeOut())) {
//
//                                    }


                                    if (!jsonObject.getBoolean("error")) {
//                                        if (LogCheck.count(LogCheck.class)<=0){
//                                            LogCheck logCheck=new LogCheck();
//                                            logCheck.setPrev_t(jsonObject.getString("prev_time"));
//                                            logCheck.setLog_hor(jsonObject.getString("loggedhour"));
//                                            logCheck.setDate(jsonObject.getString("date"));
//                                            logCheck.save();
//                                        }else {
//
//                                            LogCheck logCheck=LogCheck.findById(LogCheck.class,1);
////                                            DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd HH:mm");
////                                            TimeZone tz = TimeZone.getDefault();
////
////                                            formatter.setTimeZone(tz);
////                                            Date date = formatter.parse(logCheck.getDate()+" "+logCheck.getPrev_t());
//                                            Date dateCalss= DateUtils.getDate(logCheck.getDate()+" "+logCheck.getPrev_t());
//                                            long dateclassmill=dateCalss.getTime();
//
////                                            Calendar calendar = Calendar.getInstance();
////                                            calendar.setTime(date);
////                                            System.out.println("TIme"+ calendar.getTimeInMillis());
//
//
//                                            long new_time_millis=sessionManager.getTimerStartTime();
//                                            long diff=new_time_millis-dateclassmill;
//                                            long check=Long.parseLong(sessionManager.getTrackTimeOut())*3600*1000;
//
//
//                                            Log.d("Diff check","Start diff "+diff+" check "+check);
//                                            if (diff >=Long.parseLong(sessionManager.getTrackTimeOut())*3600*1000){
//                                                Intent intentCancel=new Intent();
//                                                Calendar cur_cal = Calendar.getInstance();
//                                                cur_cal.setTimeInMillis(System.currentTimeMillis());
//                                                cur_cal.add(Calendar.SECOND, 2);
//                                                intentCancel.setAction("CANCEL_SENDING");
//                                                pendingI = PendingIntent.getBroadcast(getApplicationContext(),1, intentCancel, 0);
//                                                alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                                                alarmMgr.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingI);
//                                            }else {
//                                                LogCheck logCheck1 = LogCheck.findById(LogCheck.class, 1);
//                                                logCheck1.setLog_hor(jsonObject.getString("loggedhour"));
//                                                logCheck1.setPrev_t(jsonObject.getString("prev_time"));
//                                                logCheck1.setDate(jsonObject.getString("date"));
//                                                logCheck1.save();
//                                            }
//
//                                        }
//
//
//
//                                        if (Long.parseLong(jsonObject.getString("loggedhour"))>= Long.parseLong(sessionManager.getTrackTimeOut())*3600*1000){
//                                            Intent intentCancel=new Intent();
//                                            Calendar cur_cal = Calendar.getInstance();
//                                            cur_cal.setTimeInMillis(System.currentTimeMillis());
//                                            cur_cal.add(Calendar.SECOND, 2);
//                                            intentCancel.setAction("CANCEL_SENDING");
//                                            pendingI = PendingIntent.getBroadcast(getApplicationContext(),1, intentCancel, 0);
//                                            alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                                            alarmMgr.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingI);
//                                        }

                                    } else {

                                        Toast.makeText(mContext,"Else",Toast.LENGTH_SHORT).show();
                                    }



                                    if (sessionManager.getLoggedHours()<=Long.parseLong(sessionManager.getTrackTimeOut())*3600*1000){

                                    }else {


                                        Intent intentCancel=new Intent();
                                        Calendar cur_cal = Calendar.getInstance();
                                        cur_cal.setTimeInMillis(System.currentTimeMillis());
                                        cur_cal.add(Calendar.SECOND, 2);
                                        intentCancel.setAction("CANCEL_SENDING");
                                        PendingIntent pendingI = PendingIntent.getBroadcast(getApplicationContext(),1, intentCancel, 0);
                                        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                        alarmMgr.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), 1, pendingI);

                                    }



                                }catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });




//                        final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
//                        alertDialogBuilder.setPositiveButton("I'M SURE", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//
////                                final ProgressDialog progressDialog=new ProgressDialog(mContext);
////                                progressDialog.setTitle("Logging out");
////                                progressDialog.setMessage("Please wait...");
////                                progressDialog.show();
////
////                                new Handler().postDelayed(new Runnable() {
////                                    @Override
////                                    public void run() {
////
////                                        progressDialog.dismiss();
////                                        sessionManager.clear();
////                                        startActivity(new Intent(NewLocation.this,LoginForm.class));
////                                        finish();
////
////                                    }
////                                },3000);
//                            }
//                        });
//                        alertDialogBuilder.setNegativeButton("NOT SURE", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        });
//                        final AlertDialog alertDialog = alertDialogBuilder.create();
//                        alertDialog.setTitle("Log out?");
//                        alertDialog.show();



//                    alarm.cancel(pintent);
//                    pintent.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;


            case R.id.iv_checkout:
                try {
                    final GPSTracker gpsTracker=new GPSTracker(mContext);
//                Intent intent=new Intent(NewLocation.this,CheckOutActivity.class);
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.check_out_dialog, null);

                    dialogBuilder.setView(dialogView);
                    edt_despcrition= (TextInputLayout) dialogView.findViewById(R.id.edt_despcrition);
                    btnSubmit= (Button) dialogView.findViewById(R.id.btnSubmit);
                    mapView= (MapView) dialogView.findViewById(R.id.mapView);
                    hsv= (ScrollView) dialogView.findViewById(R.id.hsv);


                    MapsInitializer.initialize(mContext);
                    final AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.setTitle("Checkout");

                    mapView.onCreate(alertDialog.onSaveInstanceState());
                    mapView.onResume();// needed to get the map to display immediately

                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap= googleMap ;
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.

                                return;
                            }

                            mMap.getUiSettings().setScrollGesturesEnabled(false);

                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            mMap.getUiSettings().setMapToolbarEnabled(false);
                            LatLng latLng=new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude());
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                            mMap.animateCamera(cameraUpdate);

                        }
                    });
                    alertDialog.show();


                    edt_despcrition.getEditText().setOnTouchListener(new View.OnTouchListener() {
                        public boolean onTouch(View view, MotionEvent event) {
                            // TODO Auto-generated method stub
                            if (view.getId() == R.id.edt_despcrition_text) {
                                view.getParent().requestDisallowInterceptTouchEvent(true);
                                switch (event.getAction()&MotionEvent.ACTION_MASK){
                                    case MotionEvent.ACTION_UP:
                                        view.getParent().requestDisallowInterceptTouchEvent(false);
                                        break;
                                }
                            }
                            return false;
                        }
                    });

                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (validateData()) {
                                if (gpsTracker.canGetLocation()) {

                                    final ProgressDialog progressDialog=new ProgressDialog(mContext);
                                    progressDialog.setCancelable(false);
                                    progressDialog.setMessage("Loading...");
                                    progressDialog.show();
                                    volleyUtils.checkOut(sessionManager.getId(),String.valueOf( gpsTracker.getLatitude()), String.valueOf(gpsTracker.getLongitude()), edt_despcrition.getEditText().getText().toString(), sessionManager.getAuthToken(),Calendar.getInstance().getTime().toString(), new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject=new JSONObject(response);
                                                if (!jsonObject.getBoolean("error")){
                                                    progressDialog.dismiss();
                                                    alertDialog.dismiss();
                                                    Toast.makeText(mContext,"Checkout successfully",Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                    Toast.makeText(mContext,R.string.try_again_later,Toast.LENGTH_SHORT).show();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(mContext,R.string.try_again_later,Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                                }else {

                                    Toast.makeText(mContext,R.string.try_again_later,Toast.LENGTH_SHORT).show();
                                    //                            LatLng latLng=sessionManager.getLocation();
                                    //                            volleyUtils.checkOut(sessionManager.getId(), String.valueOf(latLng.latitude),String.valueOf(latLng.longitude), edt_despcrition.getEditText().getText().toString(), sessionManager.getAuthToken(), new Response.Listener<String>() {
                                    //                                @Override
                                    //                                public void onResponse(String response) {
                                    //                                    alertDialog.dismiss();
                                    //                                }
                                    //                            }, new Response.ErrorListener() {
                                    //                                @Override
                                    //                                public void onErrorResponse(VolleyError error) {
                                    //
                                    //                                }
                                    //                            });
                                }
                            } else {
                                edt_despcrition.setError("Invalid description");
                                edt_despcrition.setFocusable(true);
                            }
                        }


                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stopServiceListener);
//        unregisterReceiver(checkListener);


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void data(double lat, double longi) {
        Log.d("val","in data::"+lat+longi);

    }


    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp



                        if (sessionManager.getClicked())
                        {
                            starttime=sessionManager.getTimerStartTime();
                        }

                        timeInMilliseconds = Calendar.getInstance().getTime().getTime() - starttime;
                        updatedtime = timeSwapBuff + timeInMilliseconds;
                        secs = (int) (updatedtime / 1000);
                        mins = secs / 60;
                        secs = secs % 60;
                        int seconds = (int) (updatedtime / 1000) % 60 ;
                        int minutes = (int) ((updatedtime / (1000*60)) % 60);
                        hours   = (int) ((updatedtime / (1000*60*60)) % 24);
                        milliseconds = (int) (updatedtime % 1000);
                        Log.d("timer2","hr "+hours+" min "+minutes+" sec "+seconds +"\ntimeInMilliseconds::"+timeInMilliseconds);

                        if (sessionManager.getClicked()){
                            progressBarMinutes.setProgress(seconds*6);
                            progressBarHour.setProgress(hours*15);

                            progressBarHour.setText(hours +"h");
                            progressBarMinutes.setText(minutes +"m");
                        }else {

                            if (progressBarMinutes.isSpinning())
                                progressBarMinutes.stopSpinning();
                            if (progressBarHour.isSpinning())
                                progressBarHour.stopSpinning();

                        }

                    }
                });
            }
        };
    }



    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 0, 1000); //
    }

    public void stoptimertask(View v) {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (progressBarMinutes.isSpinning())
            progressBarMinutes.stopSpinning();
        if (progressBarHour.isSpinning())
            progressBarHour.stopSpinning();
    }


    private boolean validateData() {
        if (!TextUtils.isEmpty(edt_despcrition.getEditText().getText().toString()))
        {
            return true;
        }

        return false;
    }


}
