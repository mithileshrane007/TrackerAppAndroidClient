package com.example.infiny.mylocationtracker.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SystemPermissionsMarshmallowUtils {
    public static final int RECORD_PERMISSION_REQUEST_CODE = 1;
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    public static final int PERMISSION_REQUEST_CODE = 200;

    private static final int ACCESS_FINE_COARSE_LOCATION_REQUESTCODE = 4;
    Activity activity;

    public SystemPermissionsMarshmallowUtils(Activity activity) {
        this.activity = activity;
    }

    public boolean checkPermissionForRecord(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForExternalStorage(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }
    public boolean checkPermissionForReadStorage(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForCamera(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }
    public boolean checkCoarseLocations(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }
    public boolean checkFineLocations(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForRecord(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)){
            Toast.makeText(activity, "Microphone permission needed for recording. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.RECORD_AUDIO},RECORD_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForExternalStorage(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForCamera(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)){
            Toast.makeText(activity, "Camera permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForFineAndCoarseLocation(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)){
            Toast.makeText(activity, "Location permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},ACCESS_FINE_COARSE_LOCATION_REQUESTCODE);
        }
    }



    public boolean checkPermission() {
        int r1 = ContextCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION);
        int r2 = ContextCompat.checkSelfPermission(activity, CAMERA);
        int r3 = ContextCompat.checkSelfPermission(activity, RECORD_AUDIO);
        int r4 = ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);
        int r5 = ContextCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION);
        int r6 = ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        return r1 == PackageManager.PERMISSION_GRANTED  &&  r4 == PackageManager.PERMISSION_GRANTED && r5 == PackageManager.PERMISSION_GRANTED && r6== PackageManager.PERMISSION_GRANTED ;
    }
    public void requestPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE,ACCESS_COARSE_LOCATION,READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }
}
