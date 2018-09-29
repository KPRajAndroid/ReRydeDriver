package com.reryde.provider.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.reryde.provider.Helper.SharedHelper;
import com.reryde.provider.R;
import com.reryde.provider.Utilities.Utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jayakumar on 31/01/17.
 */

public class ActivityEmail extends AppCompatActivity {

    ImageView backArrow;
    FloatingActionButton nextICON;
    EditText email;
    EditText mobile_number;
    TextView register, forgetPassword;
    CountryCodePicker ccp;
    Bundle extra;
    boolean Register;
    TextView emailtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        email = (EditText) findViewById(R.id.email);
        mobile_number = (EditText) findViewById(R.id.mobile_number);
        nextICON = (FloatingActionButton) findViewById(R.id.nextIcon);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        register = (TextView) findViewById(R.id.register);
        forgetPassword = (TextView) findViewById(R.id.forgetPassword);
        emailtxt = (TextView) findViewById(R.id.emailtxt);

        extra = getIntent().getExtras();
        if(extra != null) {
            //check whether register or login flow
            Register = extra.getBoolean("signup");
            if (Register) {
                register.setVisibility(View.GONE);
                email.setVisibility(View.VISIBLE);
                emailtxt.setVisibility(View.VISIBLE);
            }
        }else {
            emailtxt.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
            register.setVisibility(View.GONE);
        }

        nextICON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Register) {
                    if (mobile_number.getText().toString().length()==0){
                        Toast.makeText(ActivityEmail.this,"Please Enter a Mobile Number",Toast.LENGTH_SHORT).show();
                    }else if (mobile_number.getText().toString().length()>10){
                        Toast.makeText(ActivityEmail.this,"Please Enter a valid Mobile Number",Toast.LENGTH_SHORT).show();
                    }else if (email.getText().toString().length()==0){
                        Toast.makeText(ActivityEmail.this,"Please Enter your Email",Toast.LENGTH_SHORT).show();
                    }else if (!Utilities.isValidEmail(email.getText().toString())){
                        Toast.makeText(ActivityEmail.this,"Please Enter a Valid Email",Toast.LENGTH_SHORT).show();
                    }else {
                        Utilities.hideKeyboard(ActivityEmail.this);
                        //    SharedHelper.putKey(mContext, "mobile", "+" + ccp.getSelectedCountryCode().toString() + email.getText().toString());
                        Intent mainIntent = new Intent(ActivityEmail.this, RegisterActivity.class);
                        mainIntent.putExtra("mobile", "+" + ccp.getSelectedCountryCode().toString()+mobile_number.getText().toString());
                        mainIntent.putExtra("signup", true);
                        mainIntent.putExtra("email", email.getText().toString());
                        startActivity(mainIntent);
//                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }
                }else {
                    if (mobile_number.getText().toString().length()==0){
                        Toast.makeText(ActivityEmail.this,"Please Enter a Mobile Number",Toast.LENGTH_SHORT).show();
                    }else if (mobile_number.getText().toString().length()>10){
                        Toast.makeText(ActivityEmail.this,"Please Enter a valid Mobile Number",Toast.LENGTH_SHORT).show();
                    }else {
                        Utilities.hideKeyboard(ActivityEmail.this);
                        SharedHelper.putKey(ActivityEmail.this, "mobile", "+" + ccp.getSelectedCountryCode().toString() + mobile_number.getText().toString());
                        Intent mainIntent = new Intent(ActivityEmail.this, ActivityPassword.class);
                        mainIntent.putExtra("mobile", "+" + ccp.getSelectedCountryCode().toString()+mobile_number.getText().toString());
                        startActivity(mainIntent);
//                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }
                }

//                Utilities.hideKeyboard(ActivityEmail.this);
//                //SharedHelper.putKey(ActivityEmail.this, "email", email.getText().toString());
//                SharedHelper.putKey(ActivityEmail.this,"mobile", "+" + ccp.getSelectedCountryCode().toString() + email.getText().toString());
//                Intent mainIntent = new Intent(ActivityEmail.this, ActivityPassword.class);
//                startActivity(mainIntent);
//                finish();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(ActivityEmail.this, "email", "");
                Intent mainIntent = new Intent(ActivityEmail.this, WelcomeScreenActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                ActivityEmail.this.finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.hideKeyboard(ActivityEmail.this);
                SharedHelper.putKey(ActivityEmail.this, "password", "");
                Intent mainIntent = new Intent(ActivityEmail.this, RegisterActivity.class);
                mainIntent.putExtra("isFromMailActivity", true);
                startActivity(mainIntent);
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.hideKeyboard(ActivityEmail.this);
                SharedHelper.putKey(ActivityEmail.this, "password", "");
                Intent mainIntent = new Intent(ActivityEmail.this, ForgetPassword.class);
                mainIntent.putExtra("isFromMailActivity", true);
                startActivity(mainIntent);
            }
        });

    }

    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        SharedHelper.putKey(ActivityEmail.this, "email", "");
        Intent mainIntent = new Intent(ActivityEmail.this, WelcomeScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        ActivityEmail.this.finish();
    }
}