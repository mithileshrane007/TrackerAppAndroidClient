package com.example.infiny.mylocationtracker.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infiny.mylocationtracker.ConfigApp.Config;
import com.example.infiny.mylocationtracker.Helpers.SessionManager;
import com.example.infiny.mylocationtracker.IDataFetch;
import com.example.infiny.mylocationtracker.Listeners.StopServiceListener;
import com.example.infiny.mylocationtracker.R;
import com.example.infiny.mylocationtracker.Utils.ProgressWheel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Calendar;
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


        Calendar time = Calendar.getInstance();
        String time_out=sessionManager.getTrackTimeOut();
        ;
        if (time_out.contains("."))
        {
            String [] time_out_arr=time_out.split("\\.");
            time.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time_out_arr[0]));
            time.set(Calendar.MINUTE, Integer.valueOf(time_out_arr[1]));

        }else {
            time.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time_out));

        }
//        time.set(Calendar.HOUR_OF_DAY, 17);
//        time.set(Calendar.MINUTE, 30);
        stopServiceListener  =new StopServiceListener();
        registerReceiver(stopServiceListener,new IntentFilter("CANCEL_SENDING"));

        Intent intentCancel=new Intent();
        intentCancel.setAction("CANCEL_SENDING");
        pendingI = PendingIntent.getBroadcast(getApplicationContext(),1, intentCancel, 0);
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC, time.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingI);

    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.d("val1","in onResume");

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
                        iv_start_stop.setImageResource(R.drawable.ic_stop_white_48dp);
                        tv_start_stop.setText("Stop");
                        progressBarMinutes.spin();
                        progressBarMinutes.setSpinSpeed(1);


                        Log.d("val", "in time star::" + starttime);
                        Calendar cur_cal = Calendar.getInstance();
                        cur_cal.setTimeInMillis(System.currentTimeMillis());
                        cur_cal.add(Calendar.SECOND, 10);
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
                        sessionManager.clearTimer();
//                    alarm.cancel(pintent);
//                    pintent.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;

        }

    }


    @Override
    protected void onDestroy() {
       super.onDestroy();
        unregisterReceiver(stopServiceListener);
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

                        progressBarMinutes.setProgress(seconds*6);
                        progressBarHour.setProgress(hours*15);

                        progressBarHour.setText(hours +"h");
                        progressBarMinutes.setText(minutes +"m");

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
    }



}
