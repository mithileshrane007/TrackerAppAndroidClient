package com.example.infiny.mylocationtracker.Listeners;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.infiny.mylocationtracker.Activities.MyIntentLocationService;

/**
 * Created by infiny on 9/3/17.
 */

public class StopServiceListener extends BroadcastReceiver {

    Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        checkAlarm();
        if (intent.getAction().equals("CANCEL_SENDING")) {
            Toast.makeText(context, "I m here", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(context, MyIntentLocationService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent1, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
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
