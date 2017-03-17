package com.example.infiny.mylocationtracker.Listeners;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.infiny.mylocationtracker.Activities.MyIntentLocationService;
import com.example.infiny.mylocationtracker.Helpers.SessionManager;

import java.util.Calendar;

import static java.lang.Long.parseLong;

/**
 * Created by infiny on 16/3/17.
 */

public class CheckListener extends BroadcastReceiver {
    Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        if (intent.getAction().equals("CHECK_TRACK")) {
            Toast.makeText(context, "I m here CHECK_TRACK", Toast.LENGTH_SHORT).show();
            SessionManager sessionManager=new SessionManager(context);
            long prev_time_millis = sessionManager.getTimerStartTime();
            long new_time_millis= Calendar.getInstance().getTimeInMillis();
            PendingIntent pendingI;
            long diff=new_time_millis-prev_time_millis;
            long chekc=Long.parseLong(sessionManager.getTrackTimeOut())*3600*1000;
            Log.d("Diff check","CheckListener diff "+diff+" check "+chekc);

            AlarmManager alarmMgr;
            try {
                if ((new_time_millis-prev_time_millis)>=(parseLong(sessionManager.getTrackTimeOut())*3600*1000)) {
                    Intent intentCancel = new Intent();
                    Calendar cur_cal = Calendar.getInstance();
                    cur_cal.setTimeInMillis(System.currentTimeMillis());
                    cur_cal.add(Calendar.SECOND, 2);
                    intentCancel.setAction("CANCEL_SENDING");
                    pendingI = PendingIntent.getBroadcast(context, 1, intentCancel, 0);
                    alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmMgr.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingI);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    public void checkAlarm()
    {
        Intent intent = new Intent(mContext, MyIntentLocationService.class);
        boolean alarmUp = (PendingIntent.getService(mContext, 0,
                intent,
               PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp)
        {
            Toast.makeText(mContext,"Alarm is already active",Toast.LENGTH_SHORT).show();
            Log.d("val", "Alarm is already active");
        }
    }
}
