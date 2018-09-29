package com.reryde.provider.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.reryde.provider.Adapter.DocumentAdapter;
import com.reryde.provider.Bean.Document;
import com.reryde.provider.Fragment.PastTrips;
import com.reryde.provider.RerydeApplication;
import com.squareup.picasso.Picasso;
import com.reryde.provider.Helper.ConnectionHelper;
import com.reryde.provider.Helper.CustomDialog;
import com.reryde.provider.Helper.SharedHelper;
import com.reryde.provider.Helper.URLHelper;
import com.reryde.provider.Helper.User;
import com.reryde.provider.R;
import com.reryde.provider.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.reryde.provider.RerydeApplication.trimMessage;

public class HistoryDetails extends AppCompatActivity {
    Activity activity;
    Context context;
    Boolean isInternet;
    ConnectionHelper helper;
    CustomDialog customDialog;
    TextView tripAmount;
    TextView tripDate;
    TextView paymentType;
    TextView tripComments;
    TextView tripProviderName;
    TextView tripSource;
    TextView tripDestination;
    TextView lblTitle;
    TextView tripId;
    TextView invoice_txt;
    TextView txt04Total;
    TextView pro_res;
    TextView lost_item_name;
    TextView lost_item_desc;
    TextView txt04AmountToPaid;
    TextView txt04AdminCommission;
    ImageView tripImg, tripProviderImg, paymentTypeImg;
    RatingBar tripProviderRating;
    LinearLayout sourceAndDestinationLayout;
    android.app.AlertDialog ResponseDialog;
    View viewLayout;
    public JSONObject jsonObject;
    ImageView backArrow;
    LinearLayout parentLayout, lnrComments, lnrInvoiceSub, lnrInvoice;
    String tag = "";
    Button btnCancelRide, btnClose, btnViewInvoice,btnFoundItem;
    View lostItemView;
    Utilities utils = new Utilities();
    TextView lblBookingID, lblDistanceCovered, lblTimeTaken,txtBasePrice,TxtDiscount,lblDiscountPrice, lblBasePrice, lblDistancePrice, lblTaxPrice,lblTipPrice,lblServicePrice,lblMinPrice;
    LinearLayout lnrBookingID,lnrDistanceTravelled, lnrTimeTaken, lnrBaseFare, lnrDistanceFare, lnrTax,lnrTip,lostItem,pro_res_lyt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);
        findViewByIdAndInitialize();
        try {
            Intent intent = getIntent();
            String post_details = intent.getExtras().getString("post_value");
            tag = intent.getExtras().getString("tag");
            jsonObject = new JSONObject(post_details);
        } catch (Exception e) {
            jsonObject = null;
            e.printStackTrace();
        }

        if (jsonObject != null) {

            if (tag.equalsIgnoreCase("past_trips")) {
                btnCancelRide.setVisibility(View.GONE);
                lnrComments.setVisibility(View.GONE);
                getRequestDetails();
                lblTitle.setText("Past Trips");
                btnViewInvoice.setVisibility(View.GONE);
            } else {
                btnCancelRide.setVisibility(View.VISIBLE);
                lnrComments.setVisibility(View.GONE);
                getUpcomingDetails();
                lblTitle.setText("Upcoming Trips");
                btnViewInvoice.setVisibility(View.GONE);
            }
        }
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void findViewByIdAndInitialize() {
        activity = HistoryDetails.this;
        context = HistoryDetails.this;
        helper = new ConnectionHelper(activity);
        isInternet = helper.isConnectingToInternet();
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        parentLayout.setVisibility(View.GONE);
        tripAmount = (TextView) findViewById(R.id.tripAmount);
        tripDate = (TextView) findViewById(R.id.tripDate);
        paymentType = (TextView) findViewById(R.id.paymentType);
        paymentTypeImg = (ImageView) findViewById(R.id.paymentTypeImg);
        tripProviderImg = (ImageView) findViewById(R.id.tripProviderImg);
        tripImg = (ImageView) findViewById(R.id.tripImg);
        tripComments = (TextView) findViewById(R.id.tripComments);
        tripProviderName = (TextView) findViewById(R.id.tripProviderName);
        tripProviderRating = (RatingBar) findViewById(R.id.tripProviderRating);
        tripSource = (TextView) findViewById(R.id.tripSource);
        tripDestination = (TextView) findViewById(R.id.tripDestination);
        invoice_txt = (TextView) findViewById(R.id.invoice_txt);
        txt04Total = (TextView) findViewById(R.id.txt04Total);
        pro_res = (TextView) findViewById(R.id.pro_res);
        lost_item_name = (TextView) findViewById(R.id.lost_item_name);
        lost_item_desc = (TextView) findViewById(R.id.lost_item_desc);
        txt04AmountToPaid = (TextView) findViewById(R.id.txt04AmountToPaid);
        txt04AdminCommission = (TextView) findViewById(R.id.txt04AdminCommission);
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        tripId = (TextView) findViewById(R.id.trip_id);
        sourceAndDestinationLayout = (LinearLayout) findViewById(R.id.sourceAndDestinationLayout);
        viewLayout = (View) findViewById(R.id.ViewLayout);
        btnCancelRide = (Button) findViewById(R.id.btnCancelRide);
        btnClose = (Button) findViewById(R.id.btnClose);
        btnViewInvoice = (Button) findViewById(R.id.btnViewInvoice);
        btnFoundItem = (Button) findViewById(R.id.btnFoundItem);
        lnrComments = (LinearLayout) findViewById(R.id.lnrComments);
        lnrInvoice = (LinearLayout) findViewById(R.id.lnrInvoice);
        lnrInvoiceSub = (LinearLayout) findViewById(R.id.lnrInvoiceSub);
        backArrow = (ImageView) findViewById(R.id.backArrow);

        lnrBookingID = (LinearLayout) findViewById(R.id.lnrBookingID);
        lnrDistanceTravelled = (LinearLayout) findViewById(R.id.lnrDistanceTravelled);
        lnrTimeTaken = (LinearLayout) findViewById(R.id.lnrTimeTaken);
        lnrBaseFare = (LinearLayout) findViewById(R.id.lnrBaseFare);
        lnrDistanceFare = (LinearLayout) findViewById(R.id.lnrDistanceFare);
        lnrTax = (LinearLayout) findViewById(R.id.lnrTax);
        lnrTip = (LinearLayout) findViewById(R.id.lnrTip);
        lostItem = (LinearLayout) findViewById(R.id.lostItem);
        pro_res_lyt = (LinearLayout) findViewById(R.id.pro_res_lyt);

        lblBookingID = (TextView) findViewById(R.id.lblBookingID);
        lblDistanceCovered = (TextView) findViewById(R.id.lblDistanceCovered);
        lblTimeTaken = (TextView) findViewById(R.id.lblTimeTaken);
        lblBasePrice = (TextView) findViewById(R.id.lblBasePrice);
        txtBasePrice = (TextView) findViewById(R.id.txtBasePrice);
        lblDiscountPrice = (TextView) findViewById(R.id.lblDiscountPrice);
        TxtDiscount = (TextView) findViewById(R.id.TxtDiscount);

        lblServicePrice = (TextView) findViewById(R.id.lblServicePrice);
        lblMinPrice = (TextView) findViewById(R.id.lblMinPrice);
        lblTaxPrice = (TextView) findViewById(R.id.lblTaxPrice);
        lblDistancePrice = (TextView) findViewById(R.id.lblDistancePrice);
        lblTipPrice = (TextView) findViewById(R.id.lblTipPrice);

        lostItemView = (View) findViewById(R.id.lostItemView);

        LayerDrawable drawable = (LayerDrawable) tripProviderRating.getProgressDrawable();
        drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        drawable.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
        drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);

        btnFoundItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowResponseDialog();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lnrInvoice.setVisibility(View.GONE);
            }
        });

        btnViewInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnrInvoice.setVisibility(View.VISIBLE);
            }
        });

        lnrInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lnrInvoice.setVisibility(View.GONE);
            }
        });

        lnrInvoiceSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.cencel_request))
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                cancelRequest();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void ShowResponseDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.response_dialog, null);

        Button submitBtn = (Button) view.findViewById(R.id.Reason_submit_btn);
        final EditText reason = (EditText) view.findViewById(R.id.response_etxt);

        builder.setView(view);
        ResponseDialog = builder.create();
        ResponseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (reason.getText().toString().length() > 0) {
                            postItem(reason.getText().toString(),SharedHelper.getKey(context,"LostItem_id"));
                    ResponseDialog.dismiss();
                }else {

                }
            }
        });
        ResponseDialog.show();
    }

    private void postItem(final String Response, final String Id) {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("provider_response", Response);
            Log.e("ResetPassword", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.SendResponse + Id, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                /*Log.v("ResetPasswordResponse", response.toString());
                try {
                    JSONObject object1 = new JSONObject(response.toString());
                    Toast.makeText(context, object1.optString("message"), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgetPassword.this, ActivityEmail.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

                getRequestDetails();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                Log.e("MyTest", "" + error);
                Log.e("MyTestError", "" + error.networkResponse);
                Log.e("MyTestError1", "" + response.statusCode);
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage("Something went wrong.");
                            }
                        } else if (response.statusCode == 401) {
                            try {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                    //Call Refresh token
                                } else {
                                    displayMessage(errorObj.optString("message"));
                                }
                            } catch (Exception e) {
                                displayMessage("Something went wrong.");
                            }

                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage("Please try again.");
                            }

                        } else {
                            displayMessage("Please try again.");
                        }

                    } catch (Exception e) {
                        displayMessage("Something went wrong.");
                    }


                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        postItem(Response,Id);
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


    private void setDetails(JSONArray response) {
        if (response != null && response.length() > 0) {
            Glide.with(activity).load(response.optJSONObject(0).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(tripImg);
            if (!response.optJSONObject(0).optString("payment").equalsIgnoreCase("null")) {
                Log.e("History Details", "onResponse: Currency" + SharedHelper.getKey(context, "currency"));
                //tripAmount.setText(SharedHelper.getKey(context, "currency") + "" + response.optJSONObject(0).optJSONObject("payment").optString("total"));
            } else {
                //tripAmount.setText(SharedHelper.getKey(context, "currency") + "" + "0");
            }
            String form;
            if (tag.equalsIgnoreCase("past_trips")) {
                form = response.optJSONObject(0).optString("assigned_at");
            } else {
                form = response.optJSONObject(0).optString("schedule_at");
            }
            try {
                tripDate.setText(getDate(form) + "th " + getMonth(form) + " " + getYear(form) + "\n" + getTime(form));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tripId.setText(response.optJSONObject(0).optString("booking_id"));
            paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
            if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")) {
                paymentTypeImg.setImageResource(R.drawable.money1);
            } else {
                paymentTypeImg.setImageResource(R.drawable.visa_icon);
            }
            if (response.optJSONObject(0).optJSONObject("user").optString("picture").startsWith("http"))
                Picasso.with(activity).load(response.optJSONObject(0).optJSONObject("user").optString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(tripProviderImg);
            else
                Picasso.with(activity).load(URLHelper.base + "storage/" + response.optJSONObject(0).optJSONObject("user").optString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(tripProviderImg);
            final JSONArray res = response;
            tripProviderImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject jsonObject = res.optJSONObject(0).optJSONObject("user");

                    User user = new User();
                    user.setFirstName(jsonObject.optString("first_name"));
                    user.setLastName(jsonObject.optString("last_name"));
                    user.setEmail(jsonObject.optString("email"));
                    if (jsonObject.optString("picture").startsWith("http"))
                        user.setImg(jsonObject.optString("picture"));
                    else
                        user.setImg(URLHelper.base + "storage/" + jsonObject.optString("picture"));
                    user.setRating(jsonObject.optString("rating"));
                    user.setMobile(jsonObject.optString("mobile"));
                    Intent intent = new Intent(context, ShowProfile.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });

            if (response.optJSONObject(0).optJSONObject("user").optString("rating") != null &&
                    !response.optJSONObject(0).optJSONObject("user").optString("rating").equalsIgnoreCase(""))
                tripProviderRating.setRating(Float.parseFloat(response.optJSONObject(0).optJSONObject("user").optString("rating")));
            else {
                tripProviderRating.setRating(0);
            }

            /*if (!response.optJSONObject(0).optString("rating").equalsIgnoreCase("null") &&
                    !response.optJSONObject(0).optJSONObject("rating").optString("user_comment").equalsIgnoreCase("")) {
                tripComments.setText(response.optJSONObject(0).optJSONObject("rating").optString("user_comment"));
            } else {
                tripComments.setText(getString(R.string.no_comments));
            }*/
            tripProviderName.setText(response.optJSONObject(0).optJSONObject("user").optString("first_name") + " " + response.optJSONObject(0).optJSONObject("user").optString("last_name"));
            if (response.optJSONObject(0).optString("s_address") == null || response.optJSONObject(0).optString("d_address") == null || response.optJSONObject(0).optString("d_address").equals("") || response.optJSONObject(0).optString("s_address").equals("")) {
                sourceAndDestinationLayout.setVisibility(View.GONE);
                viewLayout.setVisibility(View.GONE);
            } else {
                tripSource.setText(response.optJSONObject(0).optString("s_address"));
                tripDestination.setText(response.optJSONObject(0).optString("d_address"));
            }
            parentLayout.setVisibility(View.VISIBLE);
        }
    }

    public void getRequestDetails() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_HISTORY_DETAILS_API + "?request_id=" + jsonObject.optString("id"), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                customDialog.dismiss();

                utils.print("Get Trip details", response.toString());
                if (response != null && response.length() > 0) {
                    Glide.with(activity).load(response.optJSONObject(0).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(tripImg);


                    getItem();

                    /*if (response.optJSONObject(0).has("item")){
                        lostItem.setVisibility(View.VISIBLE);
                        btnFoundItem.setVisibility(View.VISIBLE);

                        if (!response.optJSONObject(0).optJSONObject("item").optString("provider_response").equalsIgnoreCase("null")){
                            pro_res_lyt.setVisibility(View.VISIBLE);
                        }else {
                            pro_res_lyt.setVisibility(View.GONE);
                        }

                    }else {
                        lostItem.setVisibility(View.GONE);
                        btnFoundItem.setVisibility(View.GONE);
                    }*/

                    if (!response.optJSONObject(0).optString("payment").equalsIgnoreCase("null")) {
                        Log.e("History Details", "onResponse: Currency" + SharedHelper.getKey(context, "currency"));
                        float trip = Float.parseFloat(response.optJSONObject(0).optJSONObject("payment").optString("payable"));
                        tripAmount.setText(SharedHelper.getKey(context, "currency") + "" + String.format("%.2f",trip));
                    } else {
                        tripAmount.setText(SharedHelper.getKey(context, "currency") + "" + "0.00");
                    }

                    if (!response.optJSONObject(0).optJSONObject("payment").optString("service_charge").equalsIgnoreCase("null")){
                        float Service = Float.parseFloat(response.optJSONObject(0).optJSONObject("payment").optString("service_charge"));
                        lblServicePrice.setText(SharedHelper.getKey(context, "currency")  + String.format("%.2f",Service));
                    }else {
                        lblServicePrice.setText(SharedHelper.getKey(context, "currency")  + "0.00");
                    }

                    float MinPrice = 0.0f;

                    if (!response.optJSONObject(0).optJSONObject("service_type").optString("minimum_charge").equalsIgnoreCase("null")){
                        MinPrice = Float.parseFloat(response.optJSONObject(0).optJSONObject("service_type").optString("minimum_charge"));
                        lblMinPrice.setText(SharedHelper.getKey(context, "currency")  + String.format("%.2f",MinPrice));
                    }else {
                        lblMinPrice.setText(SharedHelper.getKey(context, "currency")  + "0.00");
                    }
                    float distance = Float.parseFloat(response.optJSONObject(0).optJSONObject("payment").optString("distance"));
                    lblDistancePrice.setText(SharedHelper.getKey(context, "currency")  + String.format("%.2f",distance));

                    float base = Float.parseFloat(response.optJSONObject(0).optJSONObject("payment").optString("fixed"));

                    float Totalbase = base+distance;

                    txtBasePrice.setText(response.optJSONObject(0).optJSONObject("service_type").optString("name")+"("+response.optJSONObject(0).optString("distance")+"km,"+response.optJSONObject(0).optString("travel_time")+ "m) ");

                    lblBasePrice.setText(SharedHelper.getKey(context, "currency")  + String.format("%.2f",Totalbase));

                    float Tax = Float.parseFloat(response.optJSONObject(0).optJSONObject("payment").optString("tax"));
                    lblTaxPrice.setText(SharedHelper.getKey(context, "currency")  + String.format("%.2f",Tax));
                    float Tip = Float.parseFloat(response.optJSONObject(0).optJSONObject("payment").optString("tip"));
                    lblTipPrice.setText(SharedHelper.getKey(context, "currency")  + String.format("%.2f",Tip));
                    lblBookingID.setText(""+response.optJSONObject(0).optString("booking_id"));
                    lblDistanceCovered.setText(response.optJSONObject(0).optString("distance") + " KM");
                    lblTimeTaken.setText(response.optJSONObject(0).optString("travel_time") + " mins");

                    TxtDiscount.setText("ReRyde "+SharedHelper.getKey(context, "currency"));

                    float DiscountFare = Float.parseFloat(response.optJSONObject(0).optJSONObject("payment").optString("discount"));

                    lblDiscountPrice.setText(SharedHelper.getKey(context, "currency") + "" + String.format("%.2f",DiscountFare));

//                    TxtDiscount,lblDiscountPrice

                    float Total = Float.parseFloat(response.optJSONObject(0).optJSONObject("payment").optString("payable"));
                    txt04Total.setText(SharedHelper.getKey(context, "currency") + "" + String.format("%.2f",Total));
                    float AmountPaid = Float.parseFloat(response.optJSONObject(0).optJSONObject("payment").optString("payable"));
                    txt04AmountToPaid.setText(SharedHelper.getKey(context, "currency") + "" + String.format("%.2f",AmountPaid));
                    float AdminCommission = Float.parseFloat(response.optJSONObject(0).optJSONObject("payment").optString("provider_commission"));
                    txt04AdminCommission.setText("- "+SharedHelper.getKey(context, "currency") + "" + String.format("%.2f",AdminCommission));
                    String form;
                    if (tag.equalsIgnoreCase("past_trips")) {
                        form = response.optJSONObject(0).optString("assigned_at");
                    } else {
                        form = response.optJSONObject(0).optString("schedule_at");
                    }
                    try {
                        tripDate.setText(getDate(form) + "th " + getMonth(form) + " " + getYear(form) + "\n" + getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
                    if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")) {
                        paymentTypeImg.setImageResource(R.drawable.money1);
                    } else {
                        paymentTypeImg.setImageResource(R.drawable.visa_icon);
                    }
                    if (response.optJSONObject(0).optJSONObject("user").optString("picture").startsWith("http"))
                        Picasso.with(activity).load(response.optJSONObject(0).optJSONObject("user").optString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(tripProviderImg);
                    else
                        Picasso.with(activity).load(URLHelper.base + "storage/" + response.optJSONObject(0).optJSONObject("user").optString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(tripProviderImg);
                    final JSONArray res = response;
                    tripProviderImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JSONObject jsonObject = res.optJSONObject(0).optJSONObject("user");

                            User user = new User();
                            user.setFirstName(jsonObject.optString("first_name"));
                            user.setLastName(jsonObject.optString("last_name"));
                            user.setEmail(jsonObject.optString("email"));
                            if (jsonObject.optString("picture").startsWith("http"))
                                user.setImg(jsonObject.optString("picture"));
                            else
                                user.setImg(URLHelper.base + "storage/" + jsonObject.optString("picture"));
                            user.setRating(jsonObject.optString("rating"));
                            user.setMobile(jsonObject.optString("mobile"));
                            Intent intent = new Intent(context, ShowProfile.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                        }
                    });

                    tripId.setText(response.optJSONObject(0).optString("booking_id"));

                    if (response.optJSONObject(0).optJSONObject("user").optString("rating") != null &&
                            !response.optJSONObject(0).optJSONObject("user").optString("rating").equalsIgnoreCase(""))
                        tripProviderRating.setRating(Float.parseFloat(response.optJSONObject(0).optJSONObject("user").optString("rating")));
                    else {
                        tripProviderRating.setRating(0);
                    }

                    if (!response.optJSONObject(0).optString("rating").equalsIgnoreCase("null") &&
                            !response.optJSONObject(0).optJSONObject("rating").optString("user_comment").equalsIgnoreCase("")) {
                        tripComments.setText(response.optJSONObject(0).optJSONObject("rating").optString("user_comment"));
                    } else {
                        tripComments.setText(getString(R.string.no_comments));
                    }
                    tripProviderName.setText(response.optJSONObject(0).optJSONObject("user").optString("first_name") + " " + response.optJSONObject(0).optJSONObject("user").optString("last_name"));
                    if (response.optJSONObject(0).optString("s_address") == null || response.optJSONObject(0).optString("d_address") == null || response.optJSONObject(0).optString("d_address").equals("") || response.optJSONObject(0).optString("s_address").equals("")) {
                        sourceAndDestinationLayout.setVisibility(View.GONE);
                        viewLayout.setVisibility(View.GONE);
                    } else {
                        tripSource.setText(response.optJSONObject(0).optString("s_address"));
                        tripDestination.setText(response.optJSONObject(0).optString("d_address"));
                    }
                    parentLayout.setVisibility(View.VISIBLE);
                }


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
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                                e.printStackTrace();
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
                        e.printStackTrace();
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        getRequestDetails();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                utils.print("Token", "" + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        RerydeApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private void getItem() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GetLostITEM, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                customDialog.dismiss();

                if (response.length()==0) {
                    lostItem.setVisibility(View.GONE);
                    btnFoundItem.setVisibility(View.GONE);

                    /*if (!response.optJSONObject(0).optJSONObject("item").optString("provider_response").equalsIgnoreCase("null")){
                        pro_res_lyt.setVisibility(View.VISIBLE);
                    }else {
                        pro_res_lyt.setVisibility(View.GONE);
                    }*/
                } else {

                    for (int i=0;i<response.length();i++){

                        if (jsonObject.optString("id").equalsIgnoreCase(response.optJSONObject(i).optString("request_id"))){
                            if (!response.optJSONObject(i).optString("provider_response").equalsIgnoreCase("null")){
                                pro_res.setText(response.optJSONObject(i).optString("provider_response"));
                                pro_res_lyt.setVisibility(View.VISIBLE);
                                btnFoundItem.setVisibility(View.VISIBLE);
                            }else {
                                pro_res.setText("");
                                pro_res_lyt.setVisibility(View.GONE);
                                btnFoundItem.setVisibility(View.VISIBLE);
                            }

                            lost_item_name.setText(response.optJSONObject(i).optString("item"));
                            lost_item_desc.setText(response.optJSONObject(i).optString("description"));
                            SharedHelper.putKey(context,"LostItem_id",response.optJSONObject(i).optString("id"));

                            lostItem.setVisibility(View.VISIBLE);
                        }


                    }

                }



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
                            try {
                                displayMessage(errorObj.optString("message"));
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
                        getItem();
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

        RerydeApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    public void cancelRequest() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", jsonObject.optString("id"));
            utils.print("", "request_id" + jsonObject.optString("id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("CancelRequestResponse", response.toString());
                customDialog.dismiss();
                finish();
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
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                                e.printStackTrace();
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
                        e.printStackTrace();
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        cancelRequest();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                utils.print("", "Access_Token" + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        RerydeApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public void displayMessage(String toastString) {
        Snackbar.make(findViewById(R.id.parentLayout), toastString, Snackbar.LENGTH_SHORT).setAction("Action", null).show();

    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(activity, WelcomeScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    public void getUpcomingDetails() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.UPCOMING_TRIP_DETAILS + "?request_id=" + jsonObject.optString("id"), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                setDetails(response);
                utils.print("Get Upcoming Details", response.toString());
/*
                if (response != null && response.length() > 0) {
                    Glide.with(activity).load(response.optJSONObject(0).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(tripImg);
                    paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
                    String form = response.optJSONObject(0).optString("schedule_at");
                    try {
                        tripDate.setText(getDate(form) + "th " + getMonth(form) + " " + getYear(form) + "\n" + getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")) {
                        paymentTypeImg.setImageResource(R.drawable.money_icon);
                    } else {
                        paymentTypeImg.setImageResource(R.drawable.visa_icon);
                    }

                    if (response.optJSONObject(0).optJSONObject("provider").optString("avatar") != null)
                        Glide.with(activity).load(URLHelper.base + "storage/" + response.optJSONObject(0).optJSONObject("provider").optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.loading).dontAnimate().into(tripProviderImg);

                    tripProviderRating.setRating(Float.parseFloat(response.optJSONObject(0).optJSONObject("provider").optString("rating")));
                    tripProviderName.setText(response.optJSONObject(0).optJSONObject("provider").optString("first_name") + " " + response.optJSONObject(0).optJSONObject("provider").optString("last_name"));
                    if (response.optJSONObject(0).optString("s_address") == null || response.optJSONObject(0).optString("d_address") == null || response.optJSONObject(0).optString("d_address").equals("") || response.optJSONObject(0).optString("s_address").equals("")) {
                        sourceAndDestinationLayout.setVisibility(View.GONE);
                        viewLayout.setVisibility(View.GONE);
                    } else {
                        tripSource.setText(response.optJSONObject(0).optString("s_address"));
                        tripDestination.setText(response.optJSONObject(0).optString("d_address"));
                    }

                    try {
                        JSONObject serviceObj = response.optJSONObject(0).optJSONObject("service_type");
                        if (serviceObj != null) {
//                            holder.car_name.setText(serviceObj.optString("name"));
                            tripAmount.setText(SharedHelper.getKey(context, "currency") + serviceObj.optString("price"));
                            Glide.with(activity).load(serviceObj.optString("image"))
                                    .placeholder(R.drawable.car_select).error(R.drawable.car_select)
                                    .dontAnimate().into(tripProviderImg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
*/
                customDialog.dismiss();
                parentLayout.setVisibility(View.VISIBLE);

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
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                                e.printStackTrace();
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
                        e.printStackTrace();
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        getUpcomingDetails();
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

        RerydeApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    private String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    private String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }

    private String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

    private String getTime(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
        return timeName;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
