package com.example.infiny.mylocationtracker.Models;

import com.orm.SugarRecord;

/**
 * Created by infiny on 15/3/17.
 */

public class LogCheck extends SugarRecord {
    private String prev_t;
    private String log_hor;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public LogCheck() {
    }

    public LogCheck(String prev_t, String log_hor, String date) {
        this.prev_t = prev_t;
        this.log_hor = log_hor;
        this.date = date;
    }

    public String getPrev_t() {
        return prev_t;
    }

    public void setPrev_t(String prev_t) {
        this.prev_t = prev_t;
    }

    public String getLog_hor() {
        return log_hor;
    }

    public void setLog_hor(String log_hor) {
        this.log_hor = log_hor;
    }
}
