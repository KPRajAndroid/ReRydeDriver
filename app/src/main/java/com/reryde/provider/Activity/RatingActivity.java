package com.reryde.provider.Activity;

import android.content.Context;
import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.reryde.provider.Helper.CustomDialog;
import com.reryde.provider.Helper.SharedHelper;
import com.reryde.provider.Model.CityListModel;
import com.reryde.provider.Model.RatingModel;
import com.reryde.provider.R;
import com.reryde.provider.Retrofit.ApiInterface;
import com.reryde.provider.Retrofit.RetrofitClient;

import javax.xml.validation.Validator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingActivity extends AppCompatActivity {

    ImageView backArrow;
    RatingBar rat03UserRating;
    TextView lifetime_trip_txt,rated_trip_txt,five_rated_txt,rating;

    CustomDialog customDialog;
    Context context = RatingActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        FindviewById();
    }
    public void FindviewById(){

        rat03UserRating = findViewById(R.id.rat03UserRating);
        lifetime_trip_txt = findViewById(R.id.lifetime_trip_txt);
        rated_trip_txt = findViewById(R.id.rated_trip_txt);
        five_rated_txt = findViewById(R.id.five_rated_txt);
        rating = findViewById(R.id.rating);
        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getRating();
        if(SharedHelper.getKey(context, "rating")!=null && !SharedHelper.getKey(context, "rating").equalsIgnoreCase("null")){
            rat03UserRating.setRating(Float.parseFloat(SharedHelper.getKey(context, "rating")));
            rating.setText(SharedHelper.getKey(context, "rating"));
        }


    }

    public void getRating(){
        customDialog = new CustomDialog(this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        ApiInterface mApiInterface = RetrofitClient.getClient().create(ApiInterface.class);
        Call<RatingModel> call = mApiInterface.getRatingList("XMLHttpRequest","Bearer "+ SharedHelper.getKey(this, "access_token"));
        call.enqueue(new Callback<RatingModel>() {
            @Override
            public void onResponse(Call<RatingModel> call, Response<RatingModel> response) {
                customDialog.dismiss();
                if(response.isSuccessful() && response.body()!=null){

                    lifetime_trip_txt.setText(Nullpointer(String.valueOf(response.body().getLifeTimeTrips())));
                    rated_trip_txt.setText(Nullpointer(String.valueOf(response.body().getRatedTrips())));
                    five_rated_txt.setText(Nullpointer(String.valueOf(response.body().getFiveRates())));

                }else {
                    Toast.makeText(getApplicationContext(),"Something wents Wrong",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<RatingModel> call, Throwable t) {
                customDialog.dismiss();

            }
        });

    }

    public String Nullpointer(String values){
        if(values!=null&&!values.equalsIgnoreCase("null")){

            return values;
        }else {
            return "0";
        }
    }
}
