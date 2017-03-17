package com.example.infiny.mylocationtracker.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by infiny on 17/3/17.
 */

public class DateUtils {

    public static Date getDate(String OurDate)
    {
        Date date = null;
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//            Date value = formatter.parse(OurDate);
//
//            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //this format changeable
//            dateFormatter.setTimeZone(TimeZone.getDefault());
//
//            OurDate = dateFormatter.format(value);
//            date = formatter.parse(OurDate);

            date=formatter.parse(OurDate);

            //Log.d("OurDate", OurDate);
        }
        catch (Exception e)
        {
//            OurDate = "00-00-0000 00:00";
            try {

                String dateInString = "1970-01-01 00:00";
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                date= formatter.parse(dateInString);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
//        return OurDate;
        return date;
    }
}
