package com.example.infiny.mylocationtracker;

import java.io.Serializable;

/**
 * Created by infiny on 3/1/17.
 */
public interface IDataFetch  extends Serializable{
    void data(double lat,double longi);
}
