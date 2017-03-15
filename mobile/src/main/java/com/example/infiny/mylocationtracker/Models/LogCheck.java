package com.example.infiny.mylocationtracker.Models;

import com.orm.SugarRecord;

/**
 * Created by infiny on 15/3/17.
 */

public class LogCheck extends SugarRecord {
    private String prev_t,log_hor;

    public LogCheck() {
    }

    public LogCheck(String prev_t, String log_hor) {
        this.prev_t = prev_t;
        this.log_hor = log_hor;
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
