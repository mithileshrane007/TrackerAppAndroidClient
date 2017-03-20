package com.example.infiny.mylocationtracker.Listeners;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.infiny.mylocationtracker.Helpers.SessionManager;

/**
 * Created by infiny on 20/3/17.
 */
public class ClearAllDataService extends IntentService{


    public ClearAllDataService() {
        super("clear-service");
        Log.d("val","in ClearAllDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            final SessionManager sessionManager=new SessionManager(getApplicationContext());
            sessionManager.setLoggedHours(0);
            sessionManager.setLoggedHoursTemp(0);
            sessionManager.setTimerStartTime(0);
            sessionManager.setClicked(false);
            sessionManager.clearTimer();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
