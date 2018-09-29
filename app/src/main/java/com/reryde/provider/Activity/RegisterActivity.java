package com.reryde.provider.Activity;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;
import com.reryde.provider.Helper.AppHelper;
import com.reryde.provider.Helper.ConnectionHelper;
import com.reryde.provider.Helper.CustomDialog;
import com.reryde.provider.Helper.SharedHelper;
import com.reryde.provider.Helper.URLHelper;
import com.reryde.provider.Helper.VolleyMultipartRequest;
import com.reryde.provider.Model.CityListModel;
import com.reryde.provider.Model.RegisterModel;
import com.reryde.provider.Model.ServiceTypeModel;
import com.reryde.provider.RerydeApplication;
import com.reryde.provider.R;
import com.reryde.provider.Retrofit.ApiInterface;
import com.reryde.provider.Retrofit.RetrofitClient;
import com.reryde.provider.Utilities.ScalingUtilities;
import com.reryde.provider.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;

import static com.reryde.provider.RerydeApplication.trimMessage;


public class RegisterActivity extends AppCompatActivity {

    public Context context = RegisterActivity.this;
    public Activity activity = RegisterActivity.this;
    String TAG = "RegisterActivity";
    CountryCodePicker ccp;

    String device_token, device_UDID;
    ImageView backArrow;
    FloatingActionButton nextICON;
    EditText email, first_name, last_name, vehiclemodel, vehicleplate, mobile_no,mobile_number, password, vehicleyear, vehiclecolor, vehiclemake;
    TextView city_etxt;
    LinearLayout city_lyt;
    TextView vehicle_type;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Boolean fromActivity = false;
    String strViewPager = "";

    String strEmail, strMobile;

    TextView agree_link;
    CheckBox agreeCheck;
    Boolean agreeCheckVal = false;
    Uri uri;
    Uri imgLicense_uri,imgVehicle_uri,imgProfile_uri;
    Boolean isImageChanged = false;

    Boolean profileimg = false;
    Boolean drivelince = false;
    Boolean vehicledocument = false;
    ImageView imgVehicle, imgProfile, imgLicense;
    String filePath_prof,filePath_Vehi,filePath_Lice;
    private static final int SELECT_PROFILE_PHOTO = 100;
    private static final int SELECT_VEHICLE_PHOTO = 101;
    private static final int SELECT_LICENSE_PHOTO = 102;
    public static int APP_REQUEST_CODE = 99;
    public static int deviceHeight;
    public static int deviceWidth;
    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder;
    UIManager uiManager;
    String[] arrayServiceTypes;

    ArrayList<String> lstServiceNames = new ArrayList<String>();
    ArrayList<String> lstServiceIds = new ArrayList<String>();
    Boolean isPermissionGivenAlready = false;

    private String blockCharacterSet = "~#^|$%&*!()_-*.,@/";
    Utilities utils = new Utilities();

    String strServiceType = "";
    int selectedIndex = 0;

    Spinner city_spinner, Service_spinner;
    String strCityID ="0", strServiceid = "0";
    EditText referral_edit;

    private CountryPicker mCountryPicker;
    CityListModel cityListModel = null;
    CityListModel.City city= null;

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    RegisterModel registerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mCountryPicker = CountryPicker.newInstance("Select Country");

        registerModel = new RegisterModel();
        try {
            Intent intent = getIntent();
            if (intent != null) {

                if (getIntent().getExtras().containsKey("mobile")) {
                    strMobile = getIntent().getExtras().getString("mobile");
                    strEmail = getIntent().getExtras().getString("email");
                    registerotpAPI();
                }


                if (getIntent().getExtras().containsKey("viewpager")) {
                    strViewPager = getIntent().getExtras().getString("viewpager");
                }

                if (getIntent().getExtras().getBoolean("isFromMailActivity")) {
                    fromActivity = true;
                } else if (!getIntent().getExtras().getBoolean("isFromMailActivity")) {
                    fromActivity = false;
                } else {
                    fromActivity = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fromActivity = false;
        }
        findViewById();
        GetToken();

        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        agreeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (agreeCheck.isChecked()) {
                    agreeCheckVal = true;
                } else {
                    agreeCheckVal = false;
                }
            }
        });

        agree_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, TermsActivity.class));
            }
        });

        nextICON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pattern ps = Pattern.compile(".*[0-9].*");
                Matcher firstName = ps.matcher(first_name.getText().toString());
                Matcher lastName = ps.matcher(last_name.getText().toString());

                if (first_name.getText().toString().equals("") || first_name.getText().toString().equalsIgnoreCase(getString(R.string.first_name))) {
                    displayMessage(getString(R.string.first_name_empty));
                } else if (firstName.matches()) {
                    displayMessage(getString(R.string.first_name_no_number));
                } else if (last_name.getText().toString().equals("") || last_name.getText().toString().equalsIgnoreCase(getString(R.string.last_name))) {
                    displayMessage(getString(R.string.last_name_empty));
                } else if (vehiclemodel.getText().toString().equals("")) {
                    displayMessage(getString(R.string.Vehicle_model_empty));
                } else if (vehicleplate.getText().toString().equals("")) {
                    displayMessage(getString(R.string.vehicle_plate_empty));
                } else if (vehicleyear.getText().toString().equals("")) {
                    displayMessage("Enter the vehicle Year");
                } else if (lastName.matches()) {
                    displayMessage(getString(R.string.last_name_no_number));
                }  else if (email.getText().toString().equals("")) {
                    displayMessage("Please Enter The Email");
                }else if (!Utilities.isValidEmail(email.getText().toString())){
                    displayMessage("Please Enter a Valid Email");
                }  else if (!agreeCheckVal) {
                    displayMessage(getString(R.string.agree_terms_conditions));
                }else if (mobile_number.getText().toString().length()==0){
                    displayMessage("Please Enter a Mobile Number");
                }else if (mobile_number.getText().toString().length()>10) {

                    displayMessage("Please Enter a valid Mobile Number");
                }else if (vehiclemake.getText().toString().equalsIgnoreCase("")) {

                    displayMessage("Please Enter Vehicle Make");
                }else if(vehiclecolor.getText().toString().equalsIgnoreCase("")){
                    displayMessage("Please Enter a valid Color");

                } else {


                        if (isInternet) {
                        if (!profileimg) {
                            displayMessage("Please Upload Profile Picture");
                        } else if (!drivelince) {
                            displayMessage("Please Upload driving Licence");
                        } else if (!vehicledocument) {
                            displayMessage("Please Upload Vehicle Document");
                        } else if(strServiceid.equalsIgnoreCase("0")){
                            displayMessage("Please Select The Service Type");
                        }else  {

                            customDialog = new CustomDialog(RegisterActivity.this);
                            customDialog.setCancelable(false);
                            if (customDialog != null)
                                customDialog.show();

                            registerModel.setStrFirstName(first_name.getText().toString());
                            registerModel.setStrLastname(last_name.getText().toString());
                            registerModel.setStrVehicleModel(vehiclemodel.getText().toString());
                            registerModel.setStrVehicleColor(vehiclecolor.getText().toString());
                            registerModel.setStrVehicleMake(vehiclemake.getText().toString());
                            registerModel.setStrVehicleYear(vehicleyear.getText().toString());
                            registerModel.setStrVehiclNumPlate(vehicleplate.getText().toString());
                            registerModel.setStrReferral(referral_edit.getText().toString());
                            registerModel.setStrEmailid(email.getText().toString());
                            registerModel.setStrMobileNumber(mobile_number.getText().toString());
                            registerModel.setStrCountryCode("+"+ccp.getSelectedCountryCode().toString());
                            System.out.println("enter the drawable image"+imgProfile.getDrawable());
                            registerModel.setUserPTofile(AppHelper.getFileDataFromDrawable(imgProfile.getDrawable()));
                            registerModel.setVehiclePhoto(AppHelper.getFileDataFromDrawable(imgVehicle.getDrawable()));
                            registerModel.setDrivingLincence(AppHelper.getFileDataFromDrawable(imgLicense.getDrawable()));
                            registerModel.setStrService_type(strServiceid);
                            registerModel.setStrCityID(strCityID);
                            /*Intent intent = new Intent(context,OTPVerification.class);
                            intent.putExtra("registerModel",registerModel);
                            intent.putExtra("imgprofile",AppHelper.getFileDataFromDrawable(imgProfile.getDrawable()));
                            intent.putExtra("imgvehicle",AppHelper.getFileDataFromDrawable(imgVehicle.getDrawable()));
                            intent.putExtra("imglince",AppHelper.getFileDataFromDrawable(imgLicense.getDrawable()));
                            intent.putExtra("status", "register");
                            startActivity(intent);*/

                            if (!referral_edit.getText().toString().equalsIgnoreCase("")){
                                CheckReferal();
                            }else {
                                RegisterOTP();
                            }


                        }

                        //registerProfilewithPhotos();
                    } else {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    }
                }
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


    }

    private void CheckReferal() {

        JSONObject object = new JSONObject();
        try {
            object.put("referral_code", referral_edit.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CheckReferral, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                Log.e("response", "response" + response);

                RegisterOTP();

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
//                        displayMessage(errorObj.optString("message"));
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
                            /*if (errorObj.has("mobile") && errorObj.optJSONArray("mobile") != null)
                                displayMessage(errorObj.optJSONArray("mobile").opt(0).toString());
                            else if (errorObj.has("email") && errorObj.optJSONArray("email") != null)
                                displayMessage(errorObj.optJSONArray("email").opt(0).toString());*/

                            displayMessage(getString(R.string.invalid_referral));

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

    private void RegisterOTP() {

        /*customDialog = new CustomDialog(RegisterActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();*/
        JSONObject object = new JSONObject();
        final String strMobile_number = registerModel.getStrCountryCode() + registerModel.getStrMobileNumber();
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


                SharedHelper.putKey(RegisterActivity.this, "mobile", strMobile_number);

                try {
                    if (response.getString("status").equals("true")) {
                        Toast.makeText(RegisterActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();

                        GoToTOTPActivity();

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

    private void GoToTOTPActivity() {

        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();

        Intent intent = new Intent(context,OTPVerification.class);
                            intent.putExtra("registerModel",registerModel);
                            /*intent.putExtra("imgprofile",AppHelper.getFileDataFromDrawablePNG(imgProfile.getDrawable()));
                            intent.putExtra("imgvehicle",AppHelper.getFileDataFromDrawablePNG(imgVehicle.getDrawable()));
                            intent.putExtra("imglince",AppHelper.getFileDataFromDrawablePNG(imgLicense.getDrawable()));*/


                            /*intent.putExtra("filePath_prof",filePath_prof);
                            intent.putExtra("filePath_Vehi",filePath_Vehi);
                            intent.putExtra("filePath_Lice",filePath_Lice);*/

                            /*intent.putExtra("imgprofile",imgProfile_uri.toString());
                            intent.putExtra("imgvehicle",imgVehicle_uri.toString());
                            intent.putExtra("imglince",imgLicense_uri.toString());*/
                            intent.putExtra("status", "register");
                            startActivity(intent);
    }

    public void findViewById() {
        /*Spinner city_spinner,Service_spinner;
        EditText referral_edit;*/
        city_spinner = (Spinner) findViewById(R.id.city_spinner);
        Service_spinner = (Spinner) findViewById(R.id.Service_spinner);
        email = (EditText) findViewById(R.id.email);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        referral_edit = (EditText) findViewById(R.id.referral_edit);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        vehiclemodel = (EditText) findViewById(R.id.vehiclemodel);
        vehicleplate = (EditText) findViewById(R.id.vehicleplate);
        vehicle_type = (TextView) findViewById(R.id.vehicle_type);
        vehiclecolor = (EditText) findViewById(R.id.vehiclecolor);
        vehicleyear = (EditText) findViewById(R.id.vehicleyear);
        mobile_no = (EditText) findViewById(R.id.mobile_no);
        password = (EditText) findViewById(R.id.password);
        vehiclemake = (EditText) findViewById(R.id.vehiclemake);
        nextICON = (FloatingActionButton) findViewById(R.id.nextIcon);
        mobile_number = (EditText) findViewById(R.id.mobile_number);
        city_etxt = (TextView) findViewById(R.id.city_etxt);
        city_lyt = (LinearLayout) findViewById(R.id.city_lyt);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        imgLicense = (ImageView) findViewById(R.id.imgLicense);
        imgVehicle = (ImageView) findViewById(R.id.imgVehicle);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
      //  email.setText(SharedHelper.getKey(context, "email"));
        first_name.setFilters(new InputFilter[]{filter});
        last_name.setFilters(new InputFilter[]{filter});

        agreeCheck = (CheckBox) findViewById(R.id.agreeCheck);
        agree_link = (TextView) findViewById(R.id.agreeLink);

        vehicle_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayServiceTypes != null) {
                    if (arrayServiceTypes.length > 0) {
                        new MaterialDialog.Builder(RegisterActivity.this)
                                .title(R.string.choose_vehicle)
                                .items(arrayServiceTypes)
                                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        strServiceType = lstServiceIds.get(which);
                                        vehicle_type.setText(lstServiceNames.get(which));
                                        selectedIndex = which;
                                        return true;
                                    }
                                })
                                .positiveText(R.string.confirm)
                                .show();
                    } else {
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.no_services_available), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.no_services_available), Toast.LENGTH_SHORT).show();
                }
            }
        });


        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkStoragePermission()) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_PROFILE_PHOTO);
                    } else {
                        goToImageIntent(SELECT_PROFILE_PHOTO);
                    }
                } else {
                    goToImageIntent(SELECT_PROFILE_PHOTO);
                }
            }
        });

        imgVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkStoragePermission()) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_VEHICLE_PHOTO);
                    } else {
                        goToImageIntent(SELECT_VEHICLE_PHOTO);
                    }
                } else {
                    goToImageIntent(SELECT_VEHICLE_PHOTO);
                }
            }
        });

        imgLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkStoragePermission()) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_LICENSE_PHOTO);
                    } else {
                        goToImageIntent(SELECT_LICENSE_PHOTO);
                    }
                } else {
                    goToImageIntent(SELECT_LICENSE_PHOTO);
                }
            }
        });
        AllCityType();

        city_lyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(getFragmentManager(), "COUNTRY_PICKER");
            }
        });

        city_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCountryPicker.show(getFragmentManager(), "COUNTRY_PICKER");
            }
        });

    }

    ArrayList<String> countryList = new ArrayList<>();
    HashMap<Integer, String> CityHash = new HashMap<Integer, String>();
    ArrayList<String> Servicetype = new ArrayList<>();
    HashMap<Integer, String> ServiceHash = new HashMap<Integer, String>();

    public void AllCityType() {
        countryList.add("Select City");

        ApiInterface mApiInterface = RetrofitClient.getClient().create(ApiInterface.class);

        Call<CityListModel> call = mApiInterface.getCityList("XMLHttpRequest");

        call.enqueue(new Callback<CityListModel>() {
            @Override
            public void onResponse(Call<CityListModel> call, retrofit2.Response<CityListModel> response) {
                System.out.println("Enter the url response" + call.request().url());

                if (response.isSuccessful()) {
                    for (int i = 0; response.body().getCities().size() > i; i++) {

                        countryList.add(response.body().getCities().get(i).getName());
                        CityHash.put(response.body().getCities().get(i).getId(), response.body().getCities().get(i).getName());

                    }

                    RerydeApplication.WORLD_COUNTRIES = Collections.singletonList(response.body());
                    mCountryPicker.setCountriesList(response.body().getCities());
                    mCountryPicker.setListener(new CountryPickerListener() {
                        @Override
                        public void onSelectCountry(CityListModel.City country) {
                            city = country;
                            city_etxt.setText(city.getName());
                            mCountryPicker.dismiss();
                            strCityID = getKeyFromValue(CityHash, city.getName());
                            // Toast.makeText(context,strCityID,Toast.LENGTH_LONG).show();\
                            getServiceList(strCityID);
                        }

                        /*@Override
                        public void onSelectCountry(CityListModel worldCountry1) {
                            city = worldCountry1;
                            country.setText(worldCountry.getName());
                            mCountryPicker.dismiss();
                        }*/
                    });
                } else {
                    Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, countryList);
                city_spinner.setAdapter(adapter);
                city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String CityName = parent.getSelectedItem().toString();
                        if (!CityName.equalsIgnoreCase("Select City")) {
                            strCityID = getKeyFromValue(CityHash, CityName);
                            // Toast.makeText(context,strCityID,Toast.LENGTH_LONG).show();\
                            getServiceList(strCityID);
                        } else {
                            if (Servicetype != null && !Servicetype.isEmpty()) {
                                Servicetype.clear();
                                ServiceHash.clear();
                            }
                            Servicetype.add("No service type");
                            SetServiceTypeAdapter();

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                //city_spinner.setSelection(adapter.getPosition(0));//

            }

            @Override
            public void onFailure(Call<CityListModel> call, Throwable t) {

            }
        });
    }

    public static String getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o.toString();
            }
        }
        return null;
    }

    public void SetServiceTypeAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, Servicetype);
        Service_spinner.setAdapter(adapter);
        Service_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String servicename = parent.getSelectedItem().toString();
                if (!servicename.equalsIgnoreCase("No service type")) {
                    strServiceid = getKeyFromValue(ServiceHash, servicename);

                } else {
                    strServiceid = "0";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getServiceList(String City_id) {
        ApiInterface mApiInterface = RetrofitClient.getClient().create(ApiInterface.class);

        Call<ServiceTypeModel> call = mApiInterface.getServiceList("XMLHttpRequest", City_id);
        call.enqueue(new Callback<ServiceTypeModel>() {
            @Override
            public void onResponse(Call<ServiceTypeModel> call, retrofit2.Response<ServiceTypeModel> response) {

                if (response.isSuccessful()) {

                    if (!response.body().getServiceType().isEmpty()) {
                        if (Servicetype != null && !Servicetype.isEmpty()) {
                            Servicetype.clear();
                        }
                        for (int i = 0; response.body().getServiceType().size() > i; i++) {
                            Servicetype.add(response.body().getServiceType().get(i).getService().get(0).getName());
                            ServiceHash.put(response.body().getServiceType().get(i).getServiceTypeId(), response.body().getServiceType().get(i).getService().get(0).getName());
                        }

                    } else {
                        if (Servicetype != null && !Servicetype.isEmpty()) {
                            Servicetype.clear();
                            ServiceHash.clear();
                        }
                        Servicetype.add("No service type");
                    }
                    SetServiceTypeAdapter();

                } else {
                    Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ServiceTypeModel> call, Throwable t) {

            }
        });


    }

    public void checkMailAlreadyExit() {
        customDialog = new CustomDialog(RegisterActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("email", email.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CHECK_MAIL_ALREADY_REGISTERED, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                phoneLogin();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                String json = null;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);
                    utils.print("MyTestError1", "" + response.statusCode);
                    try {
                        if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                if (json.startsWith("The email has already been taken")) {
                                    displayMessage(getString(R.string.email_exist));
                                } else {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                                //displayMessage(json);
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
                        checkMailAlreadyExit();
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

    private void registerAPI() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("device_type", "android");
            object.put("device_id", device_UDID);
            object.put("device_token", device_token);
            object.put("login_by", "manual");
            object.put("first_name", first_name.getText().toString());
            object.put("last_name", last_name.getText().toString());
            object.put("service_model", vehiclemodel.getText().toString());
            object.put("service_number", vehicleplate.getText().toString());
            object.put("service_type_id", strServiceType);
            object.put("email", email.getText().toString());
            object.put("password", password.getText().toString());
            object.put("password_confirmation", password.getText().toString());
            object.put("mobile", SharedHelper.getKey(RegisterActivity.this, "mobile"));
//            object.put("picture","");
//            object.put("social_unique_id","");
            utils.print("InputToRegisterAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.register, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (customDialog != null && customDialog.isShowing())
                    customDialog.dismiss();
                utils.print("SignInResponse", response.toString());
                SharedHelper.putKey(RegisterActivity.this, "email", email.getText().toString());
                SharedHelper.putKey(RegisterActivity.this, "password", password.getText().toString());
                signIn();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (customDialog != null && customDialog.isShowing())
                    customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    utils.print("MyTestError1", "" + response.statusCode);
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        utils.print("ErrorInRegisterAPI", "" + errorObj.toString());

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("error"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            try {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                    //Call Refresh token
                                } else {
                                    displayMessage(errorObj.optString("message"));
                                }
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                if (json.startsWith("The email has already been taken")) {
                                    displayMessage(getString(R.string.email_exist));
                                } else {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                                //displayMessage(json);
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
                        registerAPI();
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

    public void signIn() {
        if (isInternet) {
            customDialog = new CustomDialog(RegisterActivity.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            try {
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", device_token);
                object.put("mobile", strMobile);
                object.put("password", password.getText().toString());
                utils.print("InputToLoginAPI", "" + object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (customDialog != null && customDialog.isShowing())
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
                    if (customDialog != null && customDialog.isShowing())
                        customDialog.dismiss();
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            utils.print("ErrorInLoginAPI", "" + errorObj.toString());

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500 || response.statusCode == 401) {
                                displayMessage(getString(R.string.something_went_wrong));
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
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
            customDialog = new CustomDialog(RegisterActivity.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.USER_PROFILE_API, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (customDialog != null && customDialog.isShowing())
                        customDialog.dismiss();
                    utils.print("GetProfile", response.toString());
                    SharedHelper.putKey(RegisterActivity.this, "id", response.optString("id"));
                    SharedHelper.putKey(RegisterActivity.this, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(RegisterActivity.this, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(RegisterActivity.this, "email", response.optString("email"));
                    if (response.optString("avatar").startsWith("http"))
                        SharedHelper.putKey(context, "picture", response.optString("avatar"));
                    else
                        SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + response.optString("avatar"));
                    SharedHelper.putKey(RegisterActivity.this, "gender", "" + response.optString("gender"));
                    SharedHelper.putKey(RegisterActivity.this, "mobile", response.optString("mobile"));
                    SharedHelper.putKey(context, "approval_status", response.optString("status"));
                    if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                        SharedHelper.putKey(context, "currency", response.optString("currency"));
                    else
                        SharedHelper.putKey(context, "currency", "$");
                    SharedHelper.putKey(context, "sos", response.optString("sos"));
                    SharedHelper.putKey(RegisterActivity.this, "loggedIn", getString(R.string.True));

                    if (response.optJSONObject("service") != null) {
                        JSONObject service = response.optJSONObject("service");
                        JSONObject serviceType = service.optJSONObject("service_type");
                        SharedHelper.putKey(context, "service", serviceType.optString("name"));
                    }
                    SharedHelper.putKey(RegisterActivity.this, "login_by", "manual");
                    GoToMainActivity();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (customDialog != null && customDialog.isShowing())
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

                                json = trimMessage(new String(response.data));
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
                            getProfile();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer " + SharedHelper.getKey(RegisterActivity.this, "access_token"));
                    return headers;
                }
            };

            RerydeApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }
    }

    public void phoneLogin() {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        uiManager = new SkinManager(SkinManager.Skin.TRANSLUCENT,
                ContextCompat.getColor(this, R.color.cancel_ride_color), R.drawable.banner, SkinManager.Tint.WHITE, 85);
        configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        configurationBuilder.setUIManager(uiManager);
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.setInitialPhoneNumber(new PhoneNumber("", mobile_no.getText().toString(), "")).build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            if (data != null) {
                AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        Log.e(TAG, "onSuccess: Account Kit" + account.getId());
                        Log.e(TAG, "onSuccess: Account Kit" + AccountKit.getCurrentAccessToken().getToken());
                        if (AccountKit.getCurrentAccessToken().getToken() != null) {
                            SharedHelper.putKey(RegisterActivity.this, "account_kit_token", AccountKit.getCurrentAccessToken().getToken());
                            PhoneNumber phoneNumber = account.getPhoneNumber();
                            String phoneNumberString = phoneNumber.toString();
                            SharedHelper.putKey(RegisterActivity.this, "mobile", phoneNumberString);
//                            registerAPI();
                            registerProfilewithPhotos();
                        } else {
                            SharedHelper.putKey(RegisterActivity.this, "account_kit_token", "");
                            SharedHelper.putKey(RegisterActivity.this, "loggedIn", getString(R.string.False));
                            SharedHelper.putKey(context, "email", "");
                            SharedHelper.putKey(context, "login_by", "");
                            SharedHelper.putKey(RegisterActivity.this, "account_kit_token", "");
                            Intent goToLogin = new Intent(RegisterActivity.this, WelcomeScreenActivity.class);
                            goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(goToLogin);
                            finish();
                        }
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        Log.e(TAG, "onError: Account Kit" + accountKitError);
                        displayMessage("" + getResources().getString(R.string.social_cancel));
                    }
                });
                if (loginResult != null) {
                    SharedHelper.putKey(this, "account_kit", getString(R.string.True));
                } else {
                    SharedHelper.putKey(this, "account_kit", getString(R.string.False));
                }
                String toastMessage;
                if (loginResult.getError() != null) {
                    toastMessage = loginResult.getError().getErrorType().getMessage();
                    // showErrorActivity(loginResult.getError());
                } else if (loginResult.wasCancelled()) {
                    toastMessage = "Login Cancelled";
                } else {
                    if (loginResult.getAccessToken() != null) {
                        Log.e(TAG, "onActivityResult: Account Kit" + loginResult.getAccessToken().toString());
                        SharedHelper.putKey(this, "account_kit", loginResult.getAccessToken().toString());
                        toastMessage = "Welcome to Reryde...";
                    } else {
                        SharedHelper.putKey(this, "account_kit", "");
                        toastMessage = String.format(
                                "Welcome to Reryde...",
                                loginResult.getAuthorizationCode().substring(0, 10));
                    }
                }
            }
        }
        if (requestCode == SELECT_PROFILE_PHOTO && resultCode == activity.RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();

            String stringUrl = uri.toString();

//            String decodeFile = new decodeFile();


            imgProfile_uri = data.getData();

            RerydeApplication.profile_pic_path = AppHelper.getPath(context, imgProfile_uri);

            try {
                isImageChanged = true;
                //bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                profileimg = true;
                Bitmap resizeImg = getBitmapFromUri(this, uri);
                if (resizeImg != null) {
                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg, AppHelper.getPath(this, uri));
//                    imgProfile.setImageBitmap(reRotateImg);
                    Glide.with(context).load(imgProfile_uri).into(imgProfile);
//                    filePath_prof= tempFileImage(context,reRotateImg,"Profile");


                    RerydeApplication.profile_pic = imgProfile;

//                    /RerydeApplication.Prof_bitmap = AppHelper.getFileDataFromDrawablePNG(imgProfile.getDrawable());


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == SELECT_VEHICLE_PHOTO && resultCode == activity.RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            imgVehicle_uri = data.getData();

            RerydeApplication.veh_pic_path = AppHelper.getPath(context, imgVehicle_uri);


            try {
                vehicledocument = true;
                isImageChanged = true;
                //bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                Bitmap resizeImg = getBitmapFromUri(this, uri);
                if (resizeImg != null) {
                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg, AppHelper.getPath(this, uri));
//                    imgVehicle.setImageBitmap(reRotateImg);
                    Glide.with(context).load(imgVehicle_uri).into(imgVehicle);
//                    filePath_Vehi= tempFileImage(context,reRotateImg,"Vehicle");

                    RerydeApplication.veh_pic = imgVehicle;

//                    RerydeApplication.veh_bitmap = AppHelper.getFileDataFromDrawablePNG(imgVehicle.getDrawable());

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == SELECT_LICENSE_PHOTO && resultCode == activity.RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            imgLicense_uri = data.getData();

            RerydeApplication.lic_pic_path = AppHelper.getPath(context, imgLicense_uri);

            try {
                isImageChanged = true;
                //bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                drivelince = true;
                Bitmap resizeImg = getBitmapFromUri(this, uri);
                if (resizeImg != null) {
                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg, AppHelper.getPath(this, uri));
//                    imgLicense.setImageBitmap(reRotateImg);
                    Glide.with(context).load(imgLicense_uri).into(imgLicense);
//                    filePath_Lice= tempFileImage(context,reRotateImg,"License");

                    RerydeApplication.lic_pic = imgLicense;

//                    RerydeApplication.lic_bitmap = AppHelper.getFileDataFromDrawablePNG(imgLicense.getDrawable());

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

   /* private String decodeFile(String path, int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/TMMFOLDER");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }*/


    private String decodeFile(String path,int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/TMMFOLDER");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }

    public static String tempFileImage(Context context, Bitmap bitmap, String name) {

        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error writing file", e);
        }

        return imageFile.getAbsolutePath();
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

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        RegisterActivity.this.finish();
    }

    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
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

    public void GoToBeginActivity() {
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(activity, ActivityEmail.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();

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
                SharedHelper.putKey(RegisterActivity.this, "email", email.getText().toString());
                SharedHelper.putKey(RegisterActivity.this, "password", password.getText().toString());
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
                object.put("first_name", first_name.getText().toString());
                object.put("last_name", last_name.getText().toString());
                object.put("email", strEmail);
                object.put("password", password.getText().toString());
                object.put("password_confirmation", password.getText().toString());
                object.put("mobile", strMobile);
                object.put("year", vehicleyear.getText().toString());
                object.put("color", vehiclecolor.getText().toString());
                object.put("service_model", vehiclemodel.getText().toString());
                object.put("service_number", vehicleplate.getText().toString());
                object.put("service_type_id", strServiceType);
                object.put("make", vehiclemake.getText().toString());
                object.put("otp", password.getText().toString());
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
                params.put("avatar", new VolleyMultipartRequest.DataPart("userImage.jpg", AppHelper.getFileDataFromDrawable(imgProfile.getDrawable()), "image/jpeg"));
                params.put("image", new VolleyMultipartRequest.DataPart("vehicleImage.jpg", AppHelper.getFileDataFromDrawable(imgVehicle.getDrawable()), "image/jpeg"));
                params.put("lic_image", new VolleyMultipartRequest.DataPart("licenseImage.jpg", AppHelper.getFileDataFromDrawable(imgLicense.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        RerydeApplication.getInstance().addToRequestQueue(volleyMultipartRequest);

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SELECT_PROFILE_PHOTO) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    if (!isPermissionGivenAlready) {
                        goToImageIntent(SELECT_PROFILE_PHOTO);
                    }
                }
            }
        }
        if (requestCode == SELECT_VEHICLE_PHOTO) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    if (!isPermissionGivenAlready) {
                        goToImageIntent(SELECT_VEHICLE_PHOTO);
                    }
                }
            }
        }
        if (requestCode == SELECT_LICENSE_PHOTO) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    if (!isPermissionGivenAlready) {
                        goToImageIntent(SELECT_LICENSE_PHOTO);
                    }
                }
            }
        }
    }

    public void goToImageIntent(int requestCode) {
        isPermissionGivenAlready = true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
    }

    private static Bitmap getBitmapFromUri(@NonNull Context context, @NonNull Uri uri) throws IOException {
        Log.e("Register Activity", "getBitmapFromUri: Resize uri" + uri);
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        assert parcelFileDescriptor != null;
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//        parcelFileDescriptor.close();
//        Log.e("Register Activity", "getBitmapFromUri: Height" + deviceHeight);
//        Log.e("Register Activity", "getBitmapFromUri: width" + deviceWidth);
//        int maxSize = Math.min(deviceHeight, deviceWidth);
        if (image != null) {
//            Log.e("Register Activity", "getBitmapFromUri: Width" + image.getWidth());
//            Log.e("Register Activity", "getBitmapFromUri: Height" + image.getHeight());
//            int inWidth = image.getWidth();
//            int inHeight = image.getHeight();
//            int outWidth;
//            int outHeight;
//            if (inWidth > inHeight) {
//                outWidth = maxSize;
//                outHeight = (inHeight * maxSize) / inWidth;
//            } else {
//                outHeight = maxSize;
//                outWidth = (inWidth * maxSize) / inHeight;
//            }
//            return Bitmap.createScaledBitmap(image, outWidth, outHeight, false);
            return image;
        } else {
            Toast.makeText(context, context.getString(R.string.valid_image), Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    private void getServiceType() {
        customDialog = new CustomDialog(RegisterActivity.this);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URLHelper.GET_SERVICE_TYPES, new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                customDialog.dismiss();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObj = response.getJSONObject(i);
                        lstServiceIds.add(jsonObj.optString("id"));
                        lstServiceNames.add(jsonObj.optString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                arrayServiceTypes = new String[lstServiceNames.size()];
                for (int i = 0; i < lstServiceNames.size(); i++) {
                    arrayServiceTypes[i] = lstServiceNames.get(i);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                errorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        RerydeApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private void errorResponse(VolleyError error) {
        String json;
        NetworkResponse response = error.networkResponse;

        if (error instanceof TimeoutError) {

        }
        if (response != null && response.data != null) {
            try {
                JSONObject errorObj = new JSONObject(new String(response.data));
                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500 || response.statusCode == 401) {
                    try {
                        if (errorObj.optString("error") != null && errorObj.optString("error").length() > 0)
                            displayMessage(errorObj.optString("error"));
                        else
                            displayMessage(getString(R.string.something_went_wrong));
                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }
                } else if (response.statusCode == 422) {
                    json = RerydeApplication.trimMessage(new String(response.data));
                    if (json != null && !json.equals("")) {
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

        }
    }

    private void registerotpAPI() {

        customDialog = new CustomDialog(RegisterActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            if (strMobile != null && strEmail != null) {
                object.put("mobile", strMobile);
                object.put("email", strEmail);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.register_otp, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                Log.e("response", "response" + response);

                getServiceType();

                SharedHelper.putKey(RegisterActivity.this, "mobile", strMobile);

                try {
                    if (response.getString("status").equals("true")) {
                        Toast.makeText(RegisterActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
                        registerAPI();
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
}
