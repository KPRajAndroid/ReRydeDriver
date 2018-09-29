package com.reryde.provider.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.reryde.provider.Fragment.ContactsList;
import com.reryde.provider.Helper.SharedHelper;
import com.reryde.provider.Helper.URLHelper;
import com.reryde.provider.R;
import com.reryde.provider.RerydeApplication;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FreeRides extends AppCompatActivity implements View.OnClickListener  {

    String TAG = "FreeRides";
    ImageView select_contact,backArrow,select_share;
    Button invite_friends;
    TextView referal_code_id, referal_amount_txt,content;
    Context context = FreeRides.this;
    String device_token, device_UDID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_rides);
        select_contact = findViewById(R.id.select_contact);
        select_share = findViewById(R.id.select_share);
        backArrow = findViewById(R.id.backArrow);
        invite_friends = findViewById(R.id.invite_friends);
        GetToken();
        getProfile();
        select_contact.setOnClickListener(this);
        select_share.setOnClickListener(this);
        invite_friends.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        referal_amount_txt = findViewById(R.id.referal_amount_txt);
        referal_code_id = findViewById(R.id.referal_code_id);
        content = findViewById(R.id.content);

        content.setText("Invite your friend to drive with ReRyde and receive a referral bonus of up to "+ SharedHelper.getKey(context, "currency")+"100.");

        referal_code_id.setText(SharedHelper.getKey(context, "referral_code"));
        if (SharedHelper.getKey(context, "referral_earning") != null && !SharedHelper.getKey(context, "referral_earning").equalsIgnoreCase("null")) {
            referal_amount_txt.setText(SharedHelper.getKey(context, "currency") + SharedHelper.getKey(context, "referral_earning"));

        } else {
            referal_amount_txt.setText(SharedHelper.getKey(context, "currency") + "0");

        }
    }


    private void moveToFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.free_run_layout,fragment,"Test Fragment");
        transaction.commit();

    }

    @Override
    public void onClick(View v) {
        Fragment fragment = new ContactsList();
        switch (v.getId()){
            case R.id.select_contact:
                moveToFragment(fragment);
                break;
            case R.id.select_share:
                navigateToShareScreen(referal_code_id.getText().toString());
                break;
            case R.id.backArrow:
                finish();
                break;
            case R.id.invite_friends:
                moveToFragment(fragment);
                break;
        }

    }

    public void GetToken() {
        try {
            if(!SharedHelper.getKey(context,"device_token").equals("") && SharedHelper.getKey(context,"device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.i(TAG, "GCM Registration Token: " + device_token);
            }else{
                device_token = ""+ FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(context, "device_token",""+FirebaseInstanceId.getInstance().getToken());
                Log.i(TAG, "Failed to complete token refresh: " + device_token);
            }
        }catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh", e);
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log.i(TAG, "Device UDID:" + device_UDID);
        }catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }

    public void getProfile() {
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.USER_PROFILE_API+"?device_type=android&device_id="+device_UDID+"&device_token="+device_token, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("enter the response"+response);
                SharedHelper.putKey(context, "id", response.optString("id"));
                SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                SharedHelper.putKey(context, "email", response.optString("email"));
                SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
                SharedHelper.putKey(context, "referral_code", response.optString("referral_code"));
                SharedHelper.putKey(context, "referral_earning", response.optString("referral_earning"));
                SharedHelper.putKey(context, "rating", response.optString("rating"));

                SharedHelper.putKey(context, "sos", response.optString("sos"));
                if (response.optString("avatar").startsWith("http"))
                    SharedHelper.putKey(context, "picture", response.optString("avatar"));
                else
                    SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + response.optString("avatar"));
                SharedHelper.putKey(context, "gender", response.optString("gender"));
                SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                SharedHelper.putKey(context, "approval_status", response.optString("status"));
                if(!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                    SharedHelper.putKey(context, "currency",response.optString("currency"));
                else
                    SharedHelper.putKey(context, "currency","$");
                SharedHelper.putKey(context, "loggedIn", getString(R.string.True));

                if (response.optJSONObject("service") != null) {
                    try {
                        JSONObject service = response.optJSONObject("service");
                        if (service.optJSONObject("service_type") != null) {
                            JSONObject serviceType = service.optJSONObject("service_type");
                            SharedHelper.putKey(context, "service", serviceType.optString("name"));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                        } else if (response.statusCode == 422) {

                            json = RerydeApplication.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
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

    }

    public void displayMessage(String toastString) {
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }

    public void navigateToShareScreen(String shareUrl) {
        try{
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String shareMessage = "Hi, I’m driving with ReRyde to earn more money. Download ReRyde Driver and start earning more money too! use my referral code : " + shareUrl+ " " + URLHelper.APP_URL;
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "Share applications not found!", Toast.LENGTH_SHORT).show();
        }
    }

}
