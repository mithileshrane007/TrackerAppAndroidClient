package com.example.infiny.mylocationtracker.Listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.infiny.mylocationtracker.Helpers.GPSTracker;
import com.example.infiny.mylocationtracker.Helpers.SessionManager;

/**
 * Created by infiny on 4/1/17.
 */

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context mContext, Intent intent) {
        GPSTracker gpsTracker=new GPSTracker(mContext);
        if(gpsTracker.canGetLocation())
        {
            SessionManager sessionManager=new SessionManager(mContext);
            Log.d("val","in data::"+gpsTracker.getLatitude()+gpsTracker.getLatitude());
            sessionManager.setLocation(gpsTracker.getLatitude(),gpsTracker.getLatitude());
//            iDataFetch.data(gpsTracker.getLatitude(),gpsTracker.getLatitude());
        }

//        Calendar cur_cal = Calendar.getInstance();
//        cur_cal.setTimeInMillis(System.currentTimeMillis());
//        cur_cal.add(Calendar.SECOND, 50);
//        Log.d("val", "Calender Set time:"+cur_cal.getTime());
//        Intent intentNew = new Intent(mContext, MyIntentLocationService.class);
//        PendingIntent pintent = PendingIntent.getService(mContext, 2, intentNew, 0);
//        AlarmManager  alarm = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cur_cal.getTimeInMillis(), 300000, pintent);

    }
}