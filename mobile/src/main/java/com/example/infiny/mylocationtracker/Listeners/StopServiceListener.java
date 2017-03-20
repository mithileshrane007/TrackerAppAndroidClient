package com.example.infiny.mylocationtracker.Listeners;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.infiny.mylocationtracker.Activities.MyIntentLocationService;

import java.util.Calendar;

/**
 * Created by infiny on 9/3/17.
 */

public class StopServiceListener extends BroadcastReceiver {

    Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (intent.getAction().equals("CANCEL_SENDING")) {
            Toast.makeText(context, "I m here StopServiceListener", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(context, MyIntentLocationService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent1, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();

            // Clear all data after 24hrs
            Intent intentClear = new Intent(context, ClearAllDataService.class);
            PendingIntent pendingI = PendingIntent.getService(context, 5, intentClear, 0);
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Calendar cur_cal = Calendar.getInstance();
            cur_cal.setTimeInMillis(System.currentTimeMillis());
            cur_cal.add(Calendar.HOUR, 23);
            cur_cal.add(Calendar.MINUTE, 50);
            alarmMgr.setExact(AlarmManager.RTC, cur_cal.getTimeInMillis(), pendingI);

        }
        checkAlarm();


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
