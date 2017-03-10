package com.example.infiny.mylocationtracker.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.infiny.mylocationtracker.Helpers.SessionManager;
import com.example.infiny.mylocationtracker.Interfaces.NetworkResponse;
import com.example.infiny.mylocationtracker.NetworkUtils.VolleyUtils;
import com.example.infiny.mylocationtracker.R;
import com.example.infiny.mylocationtracker.Utils.ProgressWheel;

import org.json.JSONObject;

/**
 * Created by infiny on 4/1/17.
 */

public class LoginForm extends AppCompatActivity implements View.OnClickListener {

    EditText input_email;
    AppCompatButton btn_login;
    private Context mContext;
    ProgressWheel progressLogin;
    SessionManager sessionManager;
    private VolleyUtils volleyUtils;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext=this;
        sessionManager=new SessionManager(mContext);
        volleyUtils=new VolleyUtils();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        }
        setUi();
        btn_login.setOnClickListener(this);
    }

    private void setUi() {
        input_email= (EditText) findViewById(R.id.input_email);
        btn_login= (AppCompatButton) findViewById(R.id.btn_login);
        progressLogin= (ProgressWheel) findViewById(R.id.progressLogin);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_login:


                btn_login.setEnabled(false);
                final ProgressDialog progressDialog=new ProgressDialog(mContext);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.progress_wheel, null);
                progressDialog.setView(dialogView);
                progressLogin= (ProgressWheel) dialogView.findViewById(R.id.progressLogin);
                progressDialog.setMessage("Loading..");
                progressLogin.spin();
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);

                progressDialog.show();
                volleyUtils.loginTarget(input_email.getText().toString(), new NetworkResponse() {
                    @Override
                    public void receiveResult(Object result) {
                        try {

                            if (progressLogin.isSpinning()) {
                                progressLogin.stopSpinning();
                                btn_login.setEnabled(true);
                                progressDialog.dismiss();
                            }

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
                                sessionManager.setAuthToken(jsonObject1.getString("auth_token"));
                                intent.putExtras(bundle);
                                sessionManager.setId(jsonObject1.getString("tracking_id"));
                                sessionManager.setLogin(true);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(mContext,R.string.login_error_msg,Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressLogin.isSpinning()) {
                            btn_login.setEnabled(true);
                            progressLogin.stopSpinning();
                            progressDialog.dismiss();

                        }
                        Toast.makeText(mContext,R.string.login_error_msg,Toast.LENGTH_SHORT).show();

                    }
                });



                break;
        }
    }
}
