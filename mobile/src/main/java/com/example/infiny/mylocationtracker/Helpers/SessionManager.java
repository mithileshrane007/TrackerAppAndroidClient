package com.example.infiny.mylocationtracker.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by infiny on 21/12/16.
 */

public class SessionManager {
    private static final String CLICKED = "clicked";
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "Near";


    //Keys names
    private String CURRENT_LOC_LAT="latit";
    private String CURRENT_LOC_LONG="long";
    private String STORE_TIME="str_time";
    private String TIMERSTARTTIME="timerStartTime";
    private String TRACKERID="trackerid";
    private String ISLOGIN="login";
    private String AUTHTOKEN = "authToken";


    private String NAME = "name";
    private String USER_EMAIL = "user_email";
    private String IMAGE = "image";
    private String TRACK_TIME_OUT="tracktimeout";
    private String TRACK_TIME_INTERVAL = "tracktimeinterval";


    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void setLocation(double latit,double longi)
    {

        editor.putFloat(CURRENT_LOC_LAT, ((float) latit));
        editor.putFloat(CURRENT_LOC_LONG, ((float) longi));
        editor.commit();
    }
    public LatLng getLocation()
    {
        LatLng latLng=new LatLng(pref.getFloat(CURRENT_LOC_LAT,0f),pref.getFloat(CURRENT_LOC_LONG,0f));
        return latLng;
    }

    public void setLogin(boolean isLogin)
    {

        editor.putBoolean(ISLOGIN,isLogin);
        editor.commit();
    }
    public boolean isLogin()
    {

        return pref.getBoolean(ISLOGIN,false);
    }
    public void clear()
    {
        editor.clear();
        editor.commit();
    }




    public void setClicked(boolean clicked) {
        editor.putBoolean(CLICKED,clicked);
        editor.commit();
    }
    public boolean getClicked() {
        return pref.getBoolean(CLICKED,false);
    }


    public void setTimerStartTime(long timerStartTime) {
        editor.putLong(TIMERSTARTTIME,timerStartTime);
        editor.commit();
    }
    public long  getTimerStartTime() {
        return pref.getLong(TIMERSTARTTIME,0);
    }

    public void setId(String id) {
        editor.putString(TRACKERID,id);
        editor.commit();
    }

    public void clearTimer() {
        editor.putString(TIMERSTARTTIME,"");
        editor.commit();
    }

    public String getId() {
        return pref.getString(TRACKERID,"");
    }

    public void setUser(String name, String email, String image) {
        editor.putString(NAME,name);
        editor.putString(USER_EMAIL,email);
        editor.putString(IMAGE,image);
        editor.commit();
    }


    public ArrayList<String> getUser() {
        ArrayList<String> strings=new ArrayList<String>();
        strings.add(pref.getString(NAME,""));
        strings.add(pref.getString(USER_EMAIL,""));
        strings.add(pref.getString(IMAGE,""));

        return strings;
    }

    public void setTrackTimeInterval(String track_time_interval) {
        editor.putString(TRACK_TIME_INTERVAL,track_time_interval);
    }

    public void setTrackTimeOut(String track_time_out) {
        editor.putString(TRACK_TIME_OUT,track_time_out);
    }


    public String getTrackTimeInterval() {
        return pref.getString(TRACK_TIME_INTERVAL,"");
    }
    public String getTrackTimeOut() {
        return pref.getString(TRACK_TIME_OUT,"");
    }

    public void setAuthToken(String authToken) {

        editor.putString(AUTHTOKEN,authToken);
    }
    public String getAuthToken() {
        return pref.getString(AUTHTOKEN,"");
    }
}
