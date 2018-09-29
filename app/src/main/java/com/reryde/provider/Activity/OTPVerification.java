package com.reryde.provider.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.reryde.provider.Helper.AppHelper;
import com.reryde.provider.Helper.ConnectionHelper;
import com.reryde.provider.Helper.CustomDialog;
import com.reryde.provider.Helper.SharedHelper;
import com.reryde.provider.Helper.URLHelper;
import com.reryde.provider.Helper.VolleyMultipartRequest;
import com.reryde.provider.Model.RegisterModel;
import com.reryde.provider.R;
import com.reryde.provider.RerydeApplication;
import com.reryde.provider.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import me.philio.pinentry.PinEntryView;

import static com.reryde.provider.RerydeApplication.trimMessage;

public class OTPVerification extends AppCompatActivity implements View.OnClickListener {

    ImageView backArrow;

    PinEntryView pinView;

    TextView resend_otp_txt, Sign_txt;

    Button confirm_btn;

    String strStatus, strMobile_number;

    RegisterModel registerModel;
    byte[] imgprofile, imgvehicle, imgdrive;
    String s_profile, s_vehicle, s_drive;
    Bitmap bitmap_prof, bitmap_vehi, bitmap_dri;
    Boolean isInternet;
    ConnectionHelper helper;
    Context context = OTPVerification.this;
    Activity activity = OTPVerification.this;
    String TAG = "OTPVerification";
    CustomDialog customDialog;
    String device_token, device_UDID;

    Utilities utils = new Utilities();

    ImageView imageView, imageView1, imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        GetToken();
        FindviewById();

        Intent intent = getIntent();
        strStatus = intent.getStringExtra("status");
        if (strStatus.equalsIgnoreCase("login")) {
            Sign_txt.setVisibility(View.GONE);
            strMobile_number = intent.getStringExtra("mobile_number");
//            loginOTPAPI();

        } else {
            Sign_txt.setVisibility(View.GONE);
            registerModel = intent.getExtras().getParcelable("registerModel");

            imgprofile = RerydeApplication.Prof_bitmap;
            imgvehicle = RerydeApplication.veh_bitmap;
            imgdrive = RerydeApplication.lic_bitmap;

            strMobile_number = registerModel.getStrCountryCode() + registerModel.getStrMobileNumber();

//            registerotpAPI();
        }

    }

    public void FindviewById() {
        backArrow = findViewById(R.id.backArrow);
        pinView = findViewById(R.id.pinView);
        resend_otp_txt = findViewById(R.id.resend_otp_txt);
        confirm_btn = findViewById(R.id.confirm_btn);
        Sign_txt = findViewById(R.id.Sign_txt);
        Sign_txt.setOnClickListener(this);
        confirm_btn.setOnClickListener(this);
        resend_otp_txt.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        String first = "Don't Have an Account? ";
        String next = "<font color='#BD081C'>Signup</font>";
        Sign_txt.setText(Html.fromHtml(first + next));


    }

    public void displayMessage(String toastString) {
        // utils.print("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private void loginOTPAPI() {

        customDialog = new CustomDialog(OTPVerification.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            if (SharedHelper.getKey(OTPVerification.this, "mobile") != null) {
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
                displayMessage(response.optString("message"));
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
                                displayMessage(errorObj.optString("mobile"));
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
                        loginOTPAPI();
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


    public void GetToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                utils.print(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "" + FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(context, "device_token", "" + FirebaseInstanceId.getInstance().getToken());
                utils.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            utils.print(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                onBackPressed();
                break;
            case R.id.resend_otp_txt:
                if (strStatus.equalsIgnoreCase("login")) {
                    loginOTPAPI();
                } else {
                    Sign_txt.setVisibility(View.GONE);
                    registerotpAPI();
                }
                break;
            case R.id.confirm_btn:
                if (pinView.getText().toString().length() == 0) {
                    displayMessage(getString(R.string.otp_validation));

                } else {
                    if (isInternet) {
                        if (strStatus.equalsIgnoreCase("Login")) {
                            SharedHelper.putKey(context, "password", pinView.getText().toString());
                            signIn();
                        } else {
                            SharedHelper.putKey(context, "password", pinView.getText().toString());
                            registerProfilewithPhotos();
                        }
                    } else {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    }

                }
                break;
            case R.id.Sign_txt:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;
        }

    }

    private void signIn() {
        if (isInternet) {
            customDialog = new CustomDialog(activity);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            try {
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", device_token);
                object.put("mobile", strMobile_number);
                object.put("password", SharedHelper.getKey(context, "password"));
                utils.print("InputToLoginAPI", "" + object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    utils.print("SignUpResponse", response.toString());
                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                    if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                        SharedHelper.putKey(context, "currency", response.optString("currency"));
                    else
                        SharedHelper.putKey(context, "currency", "$");
                    getProfile();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);

                    if (response != null && response.data != null) {
                        utils.print("MyTestError1", "" + response.statusCode);
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                displayMessage(getString(R.string.something_went_wrong));
                            } else if (response.statusCode == 401) {
                                displayMessage(getString(R.string.invalid_credentials));
                            } else if (response.statusCode == 422) {
                                json = RerydeApplication.trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

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
                            signIn();
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

        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void getProfile() {

        if (isInternet) {

            customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.USER_PROFILE_API + "?device_type=android&device_id=" + device_UDID + "&device_token=" + device_token, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    utils.print("GetProfile", response.toString());
                    SharedHelper.putKey(context, "id", response.optString("id"));
                    SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
                    SharedHelper.putKey(context, "rating", response.optString("rating"));
                    SharedHelper.putKey(context, "referral_code", response.optString("referral_code"));
                    SharedHelper.putKey(context, "referral_earning", response.optString("referral_earning"));
                    SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(context, "email", response.optString("email"));
                    SharedHelper.putKey(context, "gender", "" + response.optString("gender"));
                    SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                    SharedHelper.putKey(context, "approval_status", response.optString("status"));
                    if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                        SharedHelper.putKey(context, "currency", response.optString("currency"));
                    else
                        SharedHelper.putKey(context, "currency", "$");
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.True));
                    if (response.optString("avatar").startsWith("http"))
                        SharedHelper.putKey(context, "picture", response.optString("avatar"));
                    else
                        SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + response.optString("avatar"));

                    SharedHelper.getKey(context, "picture");

                    if (response.optJSONObject("service") != null) {
                        try {
                            JSONObject service = response.optJSONObject("service");
                            if (service.optJSONObject("service_type") != null) {
                                JSONObject serviceType = service.optJSONObject("service_type");
                                SharedHelper.putKey(context, "service", serviceType.optString("name"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    SharedHelper.putKey(context, "sos", response.optString("sos"));
                    SharedHelper.putKey(OTPVerification.this, "login_by", "manual");
                    GoToMainActivity();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                displayMessage(getString(R.string.something_went_wrong));
                            } else if (response.statusCode == 401) {
                                SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                                GoToBeginActivity();
                            } else if (response.statusCode == 422) {
                                json = RerydeApplication.trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            } else if (response.statusCode == 503) {
                                displayMessage(getString(R.string.server_down));
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
                            getProfile();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            RerydeApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        ShowNumChangeAlert();
    }

    private void ShowNumChangeAlert() {
        {
            if (!isFinishing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                builder.setTitle(context.getString(R.string.app_name))
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage(getString(R.string.num_change_alert));
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Reset to previous seletion menu in navigation
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                final AlertDialog dialog = builder.create();
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    }
                });
                dialog.show();
            }
        }
    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, RegisterActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    private void registerotpAPI() {

        customDialog = new CustomDialog(OTPVerification.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {

            object.put("mobile", strMobile_number);
            object.put("email", registerModel.getStrEmailid());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.register_otp, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                Log.e("response", "response" + response);


                SharedHelper.putKey(OTPVerification.this, "mobile", strMobile_number);

                try {
                    if (response.getString("status").equals("true")) {
                        Toast.makeText(OTPVerification.this, response.getString("message"), Toast.LENGTH_LONG).show();


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        displayMessage(errorObj.optString("message"));
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
                            if (errorObj.has("mobile") && errorObj.optJSONArray("mobile") != null)
                                displayMessage(errorObj.optJSONArray("mobile").opt(0).toString());
                            else if (errorObj.has("email") && errorObj.optJSONArray("email") != null)
                                displayMessage(errorObj.optJSONArray("email").opt(0).toString());

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
                        // registerAPI();
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

    private void registerProfilewithPhotos() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.register, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if (customDialog != null && customDialog.isShowing())
                    customDialog.dismiss();
                utils.print("SignInResponse", response.toString());
                SharedHelper.putKey(OTPVerification.this, "email", registerModel.getStrEmailid());
                SharedHelper.putKey(OTPVerification.this, "password", pinView.getText().toString());
                Toast.makeText(OTPVerification.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                signIn();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && customDialog.isShowing())
                    customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.getClass().equals(TimeoutError.class)) {
                            registerProfilewithPhotos();
                            return;
                        }
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                if (errorObj.has("error")) {
                                    displayMessage(errorObj.optString("error"));
                                } else {
                                    displayMessage(errorObj.optString("message"));
                                }
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity();
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
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
                        registerProfilewithPhotos();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> object = new HashMap<>();
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", device_token);
                object.put("login_by", "manual");
                object.put("first_name", registerModel.getStrFirstName());
                object.put("last_name", registerModel.getStrLastname());
                object.put("email", registerModel.getStrEmailid());
                object.put("password", pinView.getText().toString());
                object.put("password_confirmation", pinView.getText().toString());
                object.put("mobile", strMobile_number);
                object.put("year", registerModel.getStrVehicleYear());
                object.put("color", registerModel.getStrVehicleColor());
                object.put("service_model", registerModel.getStrVehicleModel());
                object.put("service_number", registerModel.getStrVehiclNumPlate());
                object.put("service_type_id", registerModel.getStrService_type());
                object.put("make", registerModel.getStrVehicleMake());
                object.put("otp", pinView.getText().toString());
                object.put("city_id", registerModel.getStrCityID());
                object.put("referral_code", registerModel.getStrReferral());
                return object;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {

                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();

                imageView = new ImageView(activity);
                imageView1 = new ImageView(activity);
                imageView2 = new ImageView(activity);
/*
                imageView = RerydeApplication.profile_pic;
                imageView1 = RerydeApplication.veh_pic;
                imageView2 = RerydeApplication.lic_pic;*/

                String  profile_pic = RerydeApplication.profile_pic_path;
                String  veh_pic = RerydeApplication.veh_pic_path;
                String  lic_pic = RerydeApplication.lic_pic_path;



                File imgFile = new File(profile_pic);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }


                File imgFile1 = new File(veh_pic);
                if (imgFile1.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
                    imageView1.setImageBitmap(myBitmap);
                }


                File imgFile2 = new File(lic_pic);
                if (imgFile2.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());
                    imageView2.setImageBitmap(myBitmap);
                }



                params.put("avatar", new VolleyMultipartRequest.DataPart("userImage.jpg", AppHelper.getFileDataFromDrawable(imageView.getDrawable()), "image/jpeg"));
                params.put("image", new VolleyMultipartRequest.DataPart("vehicleImage.jpg", AppHelper.getFileDataFromDrawable(imageView1.getDrawable()), "image/jpeg"));
                params.put("lic_image", new VolleyMultipartRequest.DataPart("licenseImage.jpg", AppHelper.getFileDataFromDrawable(imageView2.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RerydeApplication.getInstance().addToRequestQueue(volleyMultipartRequest);


    }


}
