package com.example.infiny.mylocationtracker.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.infiny.mylocationtracker.Helpers.ConnectivityReceiver;
import com.example.infiny.mylocationtracker.Helpers.GPSTracker;
import com.example.infiny.mylocationtracker.Helpers.SessionManager;
import com.example.infiny.mylocationtracker.Listeners.ClearAllDataService;
import com.example.infiny.mylocationtracker.Models.LogSend;
import com.example.infiny.mylocationtracker.NetworkUtils.VolleyUtils;
import com.example.infiny.mylocationtracker.R;
import com.example.infiny.mylocationtracker.Utils.SystemPermissionsMarshmallowUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;


public class SplashActivity extends AppCompatActivity {



    SystemPermissionsMarshmallowUtils sysPermissionsMarshmallowUtils;
    private Context mContext;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    VolleyUtils volleyUtils;
    SessionManager sessionManager;
    private Vector<MaterialDialog> dialogs = new Vector<>();

    GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        volleyUtils=new VolleyUtils();


        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.splash_screen);

        mContext=this;
        sessionManager=new SessionManager(this);


        final TimeZone tz = TimeZone.getDefault();
        Log.d("val","TimeZone   "+tz.getDisplayName(false, TimeZone.SHORT)+" Timezon id :: " +tz.getID()+"  DST :: "+tz.getDSTSavings()+"  timestap ::"+ Calendar.getInstance().getTime().toString());

        sysPermissionsMarshmallowUtils=new SystemPermissionsMarshmallowUtils(SplashActivity.this);
        if (checkPlayServices()) {

        }else {
            return;
        }


//        Intent intent = FoursquareOAuth.getConnectIntent(mContext, getResources().getString(R.string.CLENTID_FOURSQUARE));
//        startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);

    }
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("dfdjks", "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(mContext,"onResume",Toast.LENGTH_SHORT).show();

        try {
            gpsTracker=new GPSTracker(this);
            if (gpsTracker==null) {
                gpsTracker = new GPSTracker(mContext);
            }
            if ( (Build.VERSION.SDK_INT>=23)?sysPermissionsMarshmallowUtils.checkPermission():true && ConnectivityReceiver.isConnected() && gpsTracker.canGetLocation())
            {

            }
            else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT>=23)
                            if (!sysPermissionsMarshmallowUtils.checkPermission() )
                                sysPermissionsMarshmallowUtils.requestPermission();
                        if (!ConnectivityReceiver.isConnected() )
                            showDialog();
                        if (!gpsTracker.canGetLocation())
                            gpsTracker.showSettingsAlertMaterial();
                    }
                },3000);


            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

    //                if (sysPermissionsMarshmallowUtils.checkPermission() && ConnectivityReceiver.isConnected() && gpsTracker.canGetLocation())
    //                {
    //                    startActivity(new Intent(SplashActivity.this,NewLocation.class));
    //                    finish();
    //                }

                    if (Build.VERSION.SDK_INT>=23){
                        if (sysPermissionsMarshmallowUtils.checkPermission() && ConnectivityReceiver.isConnected() && gpsTracker.canGetLocation())
                        {

                            if (LogSend.count(LogSend.class)>0)
                            {
                                List<LogSend> logSendTable = LogSend.listAll(LogSend.class);
    //                            volleyUtils.sendLocationDetailArray2(sessionManager.getId(),sessionManager.getAuthToken(),logSendTable, new Response.Listener<JSONObject>() {
    //                                @Override
    //                                public void onResponse(JSONObject response) {
    //                                    Toast.makeText(mContext,"Log send splashact respo",Toast.LENGTH_SHORT).show();
    //
    //                                }
    //                            }, new Response.ErrorListener() {
    //                                @Override
    //                                public void onErrorResponse(VolleyError error) {
    //                                    Toast.makeText(mContext,"Log send splashact errr",Toast.LENGTH_SHORT).show();
    //
    //                                }
    //                            });

                                volleyUtils.sendLocationDetailArray3(sessionManager.getId(),sessionManager.getAuthToken(), logSendTable, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Toast.makeText(getApplicationContext(),"Log send mylocserv respo",Toast.LENGTH_SHORT).show();



                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(),"Log send mylocserv error",Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }

                            if (sessionManager.isLogin())
                                startActivity(new Intent(SplashActivity.this,NewLocation.class));
                            else
                                startActivity(new Intent(SplashActivity.this,LoginForm.class));
                            sessionManager.setLocation(gpsTracker.getLatitude(),gpsTracker.getLongitude());
                            finish();
                        }
                    }
                    else
                    {
                        if (ConnectivityReceiver.isConnected() && gpsTracker.canGetLocation())
                        {
                            if (sessionManager.isLogin())
                                startActivity(new Intent(SplashActivity.this,NewLocation.class));
                            else
                                startActivity(new Intent(SplashActivity.this,LoginForm.class));
                            sessionManager.setLocation(gpsTracker.getLatitude(),gpsTracker.getLongitude());

                            if(sessionManager.getTrackTimeOut().equals("")) {
                                // Clear all data after 2 sec
                                Intent intentClear = new Intent(mContext, ClearAllDataService.class);
                                PendingIntent pendingI = PendingIntent.getService(mContext, 10, intentClear, 0);
                                AlarmManager alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                                Calendar cur_cal = Calendar.getInstance();
                                cur_cal.setTimeInMillis(System.currentTimeMillis());
                                cur_cal.add(Calendar.SECOND, 2);
                                alarmMgr.setExact(AlarmManager.RTC, cur_cal.getTimeInMillis(), pendingI);
                            }
                            finish();
                        }

                    }
                }
            },5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SystemPermissionsMarshmallowUtils.PERMISSION_REQUEST_CODE:
//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //Do something after 100ms
//                        gps = new GPSTracker(SplashScreen.this);

                onResume();
//                        EZRent.gps=new GPSTracker(HomeCategory.this);
//                        address1= LocationHelper.getLoc(EZRent.gps.getLatitude(),EZRent.gps.getLongitude(),HomeCategory.this);
//                        try {
//                            EZRent.address=address1.get(0).getLocality();
//                        }catch (Exception e)
//                        {
//                            showSettingsAlert();
//                        }
//                        Toast.makeText(getApplicationContext(), "Your Location is -"+GPSTracker.main+" \nLat: " + EZRent.gps.getLatitude() + "\nLong: " + EZRent.gps.getLongitude(), Toast.LENGTH_LONG).show();
//                        pd.dismiss();
//                    }
//                }, 5000);

                break;


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeDialogs();

    }

    public void showDialog() {
        try {
            MaterialDialog materialDialog=  new MaterialDialog.Builder(mContext)
                    .title(R.string.no_internet_connection)
                    .positiveText("Settings")
                    .negativeColor(ContextCompat.getColor(mContext,R.color.colorPrimaryDark))
                    .positiveColor(ContextCompat.getColor(mContext,R.color.colorPrimaryDark))

                    .negativeText("Exit")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
    //                        Toast.makeText(mContext,"sett",Toast.LENGTH_SHORT).show();
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

                        }
                    }).onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            closeDialogs();
                            finishAffinity();


                        }
                    }).show();

            dialogs.add(materialDialog);
            materialDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
    //                Toast.makeText(mContext,"setOnDismissListener",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            });
            materialDialog.setCancelable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (gpsTracker==null) {
            gpsTracker = new GPSTracker(mContext);
        }
        switch (requestCode) {

            case 0:
                if (Build.VERSION.SDK_INT>=23) {
                    if (sysPermissionsMarshmallowUtils.checkPermission() && ConnectivityReceiver.isConnected() && gpsTracker.canGetLocation()) {
                        onResume();
                    }
                    else {
                        if (!sysPermissionsMarshmallowUtils.checkPermission() )
                            sysPermissionsMarshmallowUtils.requestPermission();
                        if (!ConnectivityReceiver.isConnected() )
                            showDialog();
                        if (!gpsTracker.canGetLocation())
                            gpsTracker.showSettingsAlertMaterial();
                    }
                } else {

                    if ( ConnectivityReceiver.isConnected() && gpsTracker.canGetLocation()) {
                        onResume();
                    }
                    else {

                        if (!ConnectivityReceiver.isConnected())
                            showDialog();
                        if (!gpsTracker.canGetLocation())
                            gpsTracker.showSettingsAlertMaterial();
                    }

                }
                break;
        }
    }


    public void closeDialogs() {
        for (MaterialDialog dialog : dialogs)
            if (dialog.isShowing()) dialog.dismiss();
    }

}
