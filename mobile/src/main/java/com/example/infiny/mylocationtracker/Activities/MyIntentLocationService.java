package com.example.infiny.mylocationtracker.Activities;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.infiny.mylocationtracker.Helpers.GPSTracker;
import com.example.infiny.mylocationtracker.Helpers.SessionManager;
import com.example.infiny.mylocationtracker.IDataFetch;
import com.example.infiny.mylocationtracker.Models.LogSend;
import com.example.infiny.mylocationtracker.NetworkUtils.VolleyUtils;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by infiny on 3/1/17.
 */

public class MyIntentLocationService extends IntentService {


    private IDataFetch iDataFetch;

    public MyIntentLocationService() {
        // Used to name the worker thread, important only for debugging.
        super("test-service");
        Log.d("val","in MyIntentLocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("val","in onHandleIntent");
        try {



            final TimeZone tz = TimeZone.getDefault();
            Log.d("val","TimeZone   "+tz.getDisplayName(false, TimeZone.SHORT)+" Timezon id :: " +tz.getID()+"  DST :: "+tz.getDSTSavings());
            final SessionManager sessionManager=new SessionManager(getApplicationContext());

            if (sessionManager.getLoggedHours()<=Long.parseLong(sessionManager.getTrackTimeOut())*3600*1000){
                long diff_hr=sessionManager.getTimerStartTime() - Calendar.getInstance().getTime().getTime();
                sessionManager.setLoggedHours(sessionManager.getLoggedHours()+diff_hr);
            }else {
                Intent intentCancel=new Intent();
                Calendar cur_cal = Calendar.getInstance();
                cur_cal.setTimeInMillis(System.currentTimeMillis());
                cur_cal.add(Calendar.SECOND, 2);
                intentCancel.setAction("CANCEL_SENDING");
                PendingIntent pendingI = PendingIntent.getBroadcast(getApplicationContext(),1, intentCancel, 0);
                AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmMgr.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), 1, pendingI);
                return;

            }





            GPSTracker gpsTracker=new GPSTracker(getApplicationContext());
            if (gpsTracker==null) {
                gpsTracker = new GPSTracker(getApplicationContext());
            }
            VolleyUtils volleyUtils=new VolleyUtils();

            if(gpsTracker.canGetLocation())
            {
                Log.d("val","in data::"+gpsTracker.getLatitude()+gpsTracker.getLatitude());
                sessionManager.setLocation(gpsTracker.getLatitude(),gpsTracker.getLatitude());
//                String GMT="";
//                if (tz.getDisplayName(false, TimeZone.SHORT).contains("+")) {
//                    GMT="+"+tz.getDisplayName(false, TimeZone.SHORT).split("\\+")[1];
//                }
//                else {
//                    GMT="-"+tz.getDisplayName(false, TimeZone.SHORT).split("\\-")[1];
//                }

                //Mutilpe
                if (LogSend.count(LogSend.class)>0)
                {
                    List<LogSend> logSendTable = LogSend.listAll(LogSend.class);
//                    volleyUtils.sendLocationDetailArray2(sessionManager.getId(),sessionManager.getAuthToken(), logSendTable, new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            Toast.makeText(getApplicationContext(),"Log send mylocserv respo",Toast.LENGTH_SHORT).show();
//
//
//
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(getApplicationContext(),"Log send mylocserv error",Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
                    volleyUtils.sendLocationDetailArray3(sessionManager.getId(),sessionManager.getAuthToken(), logSendTable, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(),"Log send mylocserv respo",Toast.LENGTH_SHORT).show();
                            LogSend.deleteAll(LogSend.class);


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),"Log send mylocserv error",Toast.LENGTH_SHORT).show();

                            error.printStackTrace();
                            if (error instanceof TimeoutError) {

                            }
                            if( error instanceof NetworkError) {

                            }
                            if( error instanceof ServerError) {

                            }
                            if( error instanceof AuthFailureError) {

                            }
                            if( error instanceof ParseError) {

                            }
                            if( error instanceof NoConnectionError) {

                            }
                        }
                    });

                }

//
//                //Single
//                volleyUtils.sendLocationDetailArray2(sessionManager.getId(),sessionManager.getAuthToken(),gpsTracker.getLatitude(), gpsTracker.getLatitude(),TimeZone.getDefault().getID(), Calendar.getInstance().getTime().toString(),new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Toast.makeText(getApplicationContext(),"single mylocserv respo",Toast.LENGTH_SHORT).show();
//
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(),"single mylocserv error",Toast.LENGTH_SHORT).show();
//                        LogSend logSend = new LogSend(sessionManager.getId(),String.valueOf(gpsTracker.getLatitude()),String.valueOf(gpsTracker.getLatitude()),TimeZone.getDefault().getID(), Calendar.getInstance().getTime().toString());
//                        logSend.save();
//
//                        error.printStackTrace();
//
//                        if (error instanceof TimeoutError) {
//
//                        }
//                        if( error instanceof NetworkError) {
//
//                        }
//                        if( error instanceof ServerError) {
//
//                        }
//                        if( error instanceof AuthFailureError) {
//
//                        }
//                        if( error instanceof ParseError) {
//
//                        }
//                        if( error instanceof NoConnectionError) {
//
//                        }
//                    }
//                });
//


                final GPSTracker finalGpsTracker = gpsTracker;
                volleyUtils.sendLocationDetailArray3(sessionManager.getId(),sessionManager.getAuthToken(),gpsTracker.getLatitude(), gpsTracker.getLatitude(),TimeZone.getDefault().getID(), Calendar.getInstance().getTime().toString(),new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),"single mylocserv respo",Toast.LENGTH_SHORT).show();

                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"single mylocserv error",Toast.LENGTH_SHORT).show();
                        LogSend logSend = new LogSend(sessionManager.getId(),String.valueOf(finalGpsTracker.getLatitude()),String.valueOf(finalGpsTracker.getLatitude()),TimeZone.getDefault().getID(), Calendar.getInstance().getTime().toString());
                        logSend.save();

                        error.printStackTrace();

                        if (error instanceof TimeoutError) {

                        }
                        if( error instanceof NetworkError) {

                        }
                        if( error instanceof ServerError) {

                        }
                        if( error instanceof AuthFailureError) {

                        }
                        if( error instanceof ParseError) {

                        }
                        if( error instanceof NoConnectionError) {

                        }
                    }
                });
//            iDataFetch.data(gpsTracker.getLatitude(),gpsTracker.getLatitude());
            }

            gpsTracker.stopUsingGPS();
        }catch (Exception e)
        {
            Log.d("val1","in expetion outer");

            e.printStackTrace();
//            SessionManager sessionManager=new SessionManager(getApplicationContext());
//            sessionManager.setClicked(false);
        }
    }
}
