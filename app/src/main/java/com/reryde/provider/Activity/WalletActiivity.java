package com.reryde.provider.Activity;

import android.content.Context;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.reryde.provider.Helper.SharedHelper;
import com.reryde.provider.R;

public class WalletActiivity extends AppCompatActivity {

    ImageView backArrow;
    TextView wallet_amount_txt;

    Context context = WalletActiivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        FindviewById();

        if(SharedHelper.getKey(context, "currency")!=null && SharedHelper.getKey(context, "currency").equalsIgnoreCase("null")){
            wallet_amount_txt.setText(SharedHelper.getKey(context, "currency")+SharedHelper.getKey(context, "wallet_balance"));
        }else {
            wallet_amount_txt.setText(SharedHelper.getKey(context, "currency")+"0");
        }


    }
    public void FindviewById(){
        backArrow= findViewById(R.id.backArrow);
        wallet_amount_txt= findViewById(R.id.wallet_amount_txt);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
