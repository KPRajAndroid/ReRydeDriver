package com.reryde.provider.Activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hbb20.CountryCodePicker;
import com.reryde.provider.Helper.CustomDialog;
import com.reryde.provider.Helper.SharedHelper;
import com.reryde.provider.Helper.URLHelper;
import com.reryde.provider.R;
import com.reryde.provider.RerydeApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText mobile_number;
    CountryCodePicker ccp;
    Button confirm_btn;
    ImageView backArrow;
    TextView signup_txt, Sign_txt;
    CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FindviewById();
    }

    public void FindviewById() {
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        mobile_number = findViewById(R.id.mobile_number);
        backArrow = findViewById(R.id.backArrow);
        confirm_btn = findViewById(R.id.confirm_btn);
        signup_txt = findViewById(R.id.signup_txt);
        confirm_btn.setOnClickListener(this);
        signup_txt.setOnClickListener(this);
        backArrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_txt:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;
            case R.id.confirm_btn:
                if (mobile_number.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please Enter a Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (!(mobile_number.getText().toString().length() < 20)) {
                    Toast.makeText(getApplicationContext(), "Please Enter a valid Mobile Number", Toast.LENGTH_SHORT).show();
                } else {
                    /*Intent intent = new Intent(getApplicationContext(), OTPVerification.class);
                    intent.putExtra("status", "login");
                    // intent.putExtra("countrycode", ccp.getSelectedCountryCode().toString());
                    intent.putExtra("mobile_nuber", "+"+ccp.getSelectedCountryCode().toString() + mobile_number.getText().toString());
                    startActivity(intent);*/

                    LoginAPI();

                }
                break;
            case R.id.backArrow:
                finish();
                break;
        }

    }

    private void LoginAPI() {

        customDialog = new CustomDialog(LoginActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        String strMobile_number = "+"+ccp.getSelectedCountryCode().toString() + mobile_number.getText().toString();
        try {
            if (SharedHelper.getKey(LoginActivity.this, "mobile") != null) {
                object.put("mobile", strMobile_number);
            }
            Log.e("Login", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.Login_otp, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
//                displayMessage(response.optString("message"));

                Toast.makeText(LoginActivity.this,response.optString("message"),Toast.LENGTH_LONG).show();

                GoToOTPActivity();

                Log.e("response", "response" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.e("MyTest", "" + error);
                    Log.e("MyTestError", "" + error.networkResponse);
                    Log.e("MyTestError1", "" + response.statusCode);
                    Log.e("MyTestError1", "" + response.data);
                    Log.e("MyTestError1", "" + new String(response.data));
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            try {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                    //   Refresh token
                                } else {
                                    displayMessage(errorObj.optString("message"));
                                }
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 422) {
                            if (errorObj.has("mobile")) {
//                                displayMessage(errorObj.optString("mobile"));
                                Toast.makeText(LoginActivity.this,getString(R.string.invalid_email),Toast.LENGTH_LONG).show();
                                GoToRegisterActivity();
                            } else {
                                displayMessage(errorObj.optString("message"));
                            }
                            json = RerydeApplication.trimMessage(new String(response.data));
                            /*if (json != "" && json != null) {
                                if (json.startsWith("The email has already been taken")) {
                                    displayMessage(getString(R.string.email_exist));
                                }else{
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                                //displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }*/

                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }


                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        LoginAPI();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        RerydeApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void GoToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void displayMessage(String toastString) {
        // utils.print("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(LoginActivity.this, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private void GoToOTPActivity() {
    Intent intent = new Intent(LoginActivity.this, OTPVerification.class);
                    intent.putExtra("status", "login");
                    // intent.putExtra("countrycode", ccp.getSelectedCountryCode().toString());
                    intent.putExtra("mobile_number", "+"+ccp.getSelectedCountryCode().toString() + mobile_number.getText().toString());
                    startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
