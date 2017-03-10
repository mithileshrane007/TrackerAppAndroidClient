package com.example.infiny.mylocationtracker.NetworkUtils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.infiny.mylocationtracker.ConfigApp.AppActivity;
import com.example.infiny.mylocationtracker.ConfigApp.Config;
import com.example.infiny.mylocationtracker.Interfaces.NetworkResponse;
import com.example.infiny.mylocationtracker.Models.LogSend;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by infiny on 21/12/16.
 */

public class VolleyUtils {

    Context context;

    public VolleyUtils(Context context){
        this.context=context;

    }
    public VolleyUtils(){

    }


    public void getDetailedLocation(LatLng location, final NetworkResponse callback, final ErrorVolleyUtils errorCall) {

        String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+location.latitude+","+location.longitude+ "&sensor=true";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(response);
                        callback.receiveResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("surroundingPartiesV=", String.valueOf(error));

                        errorCall.onErrorResponse(error);
                    }
                });

        AppActivity.getInstance().addToRequestQueue(request);
    }



    public void loginTarget(final String id, final NetworkResponse callback, final Response.ErrorListener errorCall) {


        StringRequest request = new StringRequest(Request.Method.POST, Config.BASE_URL + Config.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.receiveResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorCall.onErrorResponse(error);
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("tracking_id", id);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> mHeaders = new android.support.v4.util.ArrayMap<String, String>();
                mHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                mHeaders.put("Accept", "application/json");

                return mHeaders;
            }
        };



        AppActivity.getInstance().addToRequestQueue(request);
    }


    public void sendLocationDetails(final double latit, final double longi, final String timezone_str, final String timezone_id, final NetworkResponse callback, final Response.ErrorListener errorCall){


        String url= Config.BASE_URL + "api_store_location";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.receiveResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorCall.onErrorResponse(error);
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("tracker_id","5");
                params.put("latitude",String.valueOf(latit));
                params.put("longitude", String.valueOf(longi));
                params.put("timezone_str",timezone_str);
                params.put("timezone_id",timezone_id);

                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> mHeaders = new android.support.v4.util.ArrayMap<String, String>();
                mHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                mHeaders.put("Accept", "application/json");

                return mHeaders;
            }

        };

        AppActivity.getInstance().addToRequestQueue(request);
    }









    public void sendLocationDetailArray(final String id, final List<LogSend> logSendTable, final NetworkResponse callback, final Response.ErrorListener errorCall){

        String url= Config.BASE_URL + "api_store_location";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.receiveResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorCall.onErrorResponse(error);
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tracker_id",id);
                for (int i=0;i<logSendTable.size();i++) {
//                    params.put("latitude["+i+"]",logSendTable.get(i).getLatitude());
//                    params.put("longitude["+i+"]",logSendTable.get(i).getLongitude());
//                    params.put("time_zone["+i+"]",logSendTable.get(i).getTimezone_str());

                }

                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> mHeaders = new android.support.v4.util.ArrayMap<String, String>();
                mHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                mHeaders.put("Accept", "application/json");

                return mHeaders;
            }

        };

        AppActivity.getInstance().addToRequestQueue(request);
    }

    public void sendLocationDetailArray2(final String id,final String authToken, final List<LogSend> logSendTable, final Response.Listener<JSONObject> callback, final Response.ErrorListener errorCall){

        String url= Config.BASE_URL + Config.LOG_SEND;

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("target_id",id);
            JSONArray jsonArray=new JSONArray();
            for (int i=0;i<logSendTable.size();i++) {
                JSONObject jsonSingle=new JSONObject();
                jsonSingle.put("latitude",logSendTable.get(i).getLatitude());
                jsonSingle.put("longitude",logSendTable.get(i).getLongitude());
                jsonSingle.put("time_zone",logSendTable.get(i).getTimezone_str());
                jsonSingle.put("created_at",logSendTable.get(i).getCreated_at());
                jsonArray.put(jsonSingle);

            }

            jsonObject.put("data",jsonArray);


        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onResponse(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                errorCall.onErrorResponse(error);
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> mHeaders = new HashMap<String, String>();
                mHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                mHeaders.put("Accept", "application/json");
                mHeaders.put("token",authToken);
                return mHeaders;
            }

        };

        AppActivity.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void sendLocationDetailArray3(final String id, final String authToken, final double latitude, final double longitude, final String timezone, final String timestamp, final Response.Listener<String> callback, final Response.ErrorListener errorCall) {

        String url= Config.BASE_URL + Config.LOG_SEND;

        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorCall.onErrorResponse(error);
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("target_id",id);

                params.put("latitude[0]",String.valueOf(latitude));
                params.put("longitude[0]",String.valueOf(longitude));
                params.put("time_zone[0]",timezone);
                params.put("created_at[0]",timestamp);


                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> mHeaders = new android.support.v4.util.ArrayMap<String, String>();
                mHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                mHeaders.put("Accept", "application/json");
                mHeaders.put("token",authToken);

                return mHeaders;
            }

        };

        AppActivity.getInstance().addToRequestQueue(request);
    }

    public void sendLocationDetailArray3(final String id, final String authToken,final List<LogSend> logSendTable , final Response.Listener<String> callback, final Response.ErrorListener errorCall) {

        String url= Config.BASE_URL + Config.LOG_SEND;

        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorCall.onErrorResponse(error);
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("target_id", id);

                for (int i=0;i<logSendTable.size();i++) {

                    params.put("latitude["+i+"]",logSendTable.get(i).getLatitude());
                    params.put("longitude["+i+"]",logSendTable.get(i).getLongitude());
                    params.put("time_zone["+i+"]",logSendTable.get(i).getTimezone_str());
                    params.put("created_at["+i+"]",logSendTable.get(i).getCreated_at());
                }

                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> mHeaders = new android.support.v4.util.ArrayMap<String, String>();
                mHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                mHeaders.put("Accept", "application/json");
                mHeaders.put("token",authToken);

                return mHeaders;
            }

        };

        AppActivity.getInstance().addToRequestQueue(request);
    }

    public void sendLocationDetailArray2(String id, final String authToken, double latitude, double longitude, String timezone, String timestamp, final Response.Listener<JSONObject> callback, final Response.ErrorListener errorCall) {

        String url= Config.BASE_URL + Config.LOG_SEND;

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("target_id",id);
            JSONArray jsonArray=new JSONArray();

            JSONObject jsonSingle=new JSONObject();
            jsonSingle.put("latitude",String.valueOf(latitude));
            jsonSingle.put("longitude",String.valueOf(longitude));
            jsonSingle.put("time_zone",timezone);
            jsonSingle.put("created_at",timestamp);
            jsonArray.put(jsonSingle);

            jsonObject.put("data",jsonArray);


        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onResponse(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                errorCall.onErrorResponse(error);
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> mHeaders = new HashMap<String, String>();
                mHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                mHeaders.put("Accept", "application/json");
                mHeaders.put("token",authToken);
                return mHeaders;
            }

        };

        AppActivity.getInstance().addToRequestQueue(jsonObjReq); }



    public void check(final NetworkResponse callback, final ErrorVolleyUtils errorCall){


        String url= Config.BASE_URL + "get_details_of_user";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.receiveResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorCall.onErrorResponse(error);
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("tracker_id","5");

                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> mHeaders = new android.support.v4.util.ArrayMap<String, String>();
                mHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                mHeaders.put("Accept", "application/json");

                return mHeaders;
            }

        };

        AppActivity.getInstance().addToRequestQueue(request);
    }



}
