package com.example.infiny.mylocationtracker.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.example.infiny.mylocationtracker.Helpers.SessionManager;
import com.example.infiny.mylocationtracker.Interfaces.NetworkResponse;
import com.example.infiny.mylocationtracker.NetworkUtils.VolleyUtils;
import com.example.infiny.mylocationtracker.R;

import org.json.JSONObject;

/**
 * Created by infiny on 4/1/17.
 */

public class LoginForm extends AppCompatActivity implements View.OnClickListener {

    EditText input_email;
    AppCompatButton btn_login;
    private Context mContext;
    SessionManager sessionManager;
    private VolleyUtils volleyUtils;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext=this;
        sessionManager=new SessionManager(mContext);
        volleyUtils=new VolleyUtils();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
//        }
        setUi();
        btn_login.setOnClickListener(this);
    }

    private void setUi() {
        input_email= (EditText) findViewById(R.id.input_email);
        btn_login= (AppCompatButton) findViewById(R.id.btn_login);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_login:


                btn_login.setEnabled(false);
                final ProgressDialog progressDialog=new ProgressDialog(mContext);

                progressDialog.setMessage("Loading..");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);

                progressDialog.show();
                volleyUtils.loginTarget(input_email.getText().toString(), new NetworkResponse() {
                    @Override
                    public void receiveResult(Object result) {
                        try {
                            btn_login.setEnabled(true);


                            progressDialog.dismiss();
                            JSONObject jsonObject=new JSONObject(result.toString());
                            if (!jsonObject.getBoolean("error")) {
                                Intent intent = new Intent(mContext, NewLocation.class);
                                Bundle bundle=new Bundle();
                                JSONObject jsonObject1=jsonObject.getJSONObject("result");
                                bundle.putString("first_name",jsonObject1.getString("first_name"));
                                bundle.putString("last_name",jsonObject1.getString("last_name"));
                                bundle.putString("image",jsonObject1.getString("image"));
                                bundle.putString("phone_no",String.valueOf(jsonObject1.getInt("phone_no")));
                                bundle.putString("email",jsonObject1.getString("email"));
                                sessionManager.setTrackTimeInterval(jsonObject1.getString("track_time_interval"));
                                sessionManager.setTrackTimeOut(jsonObject1.getString("track_time_out"));
                                sessionManager.setLoggedHours(0);
                                sessionManager.setLoggedHoursTemp(0);

                                sessionManager.setAuthToken(jsonObject1.getString("auth_token"));
                                intent.putExtras(bundle);
                                sessionManager.setId(jsonObject1.getString("tracking_id"));
                                sessionManager.setLogin(true);
                                // TODO: Move this to where you establish a user session
                                logUser();

                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(mContext,R.string.login_error_msg,Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            btn_login.setEnabled(true);

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        btn_login.setEnabled(true);

                        Toast.makeText(mContext,R.string.login_error_msg,Toast.LENGTH_SHORT).show();

                    }
                });



                break;
        }
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12345");
//        Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName(sessionManager.getId());
    }

}
