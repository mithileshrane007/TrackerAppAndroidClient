package com.example.infiny.mylocationtracker.Models;

import com.orm.SugarRecord;

/**
 * Created by infiny on 8/3/17.
 */

public class LogSend extends SugarRecord {


    private  String created_at;
    private String tracker_id,latitude,longitude,timezone_str,timezone_id;


    public LogSend() {
    }

    public LogSend(String tracker_id, String latitude, String longitude, String timezone_id, String created_at) {
        this.tracker_id = tracker_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.created_at = created_at;
        this.timezone_id = timezone_id;
    }

    public LogSend(String tracker_id, String latitude, String longitude, String timezone_id) {
        this.tracker_id = tracker_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone_id = timezone_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getTracker_id() {
        return tracker_id;
    }

    public void setTracker_id(String tracker_id) {
        this.tracker_id = tracker_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimezone_str() {
        return timezone_str;
    }

    public void setTimezone_str(String timezone_str) {
        this.timezone_str = timezone_str;
    }

    public String getTimezone_id() {
        return timezone_id;
    }

    public void setTimezone_id(String timezone_id) {
        this.timezone_id = timezone_id;
    }
}
