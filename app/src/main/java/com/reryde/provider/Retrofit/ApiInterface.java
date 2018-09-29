package com.reryde.provider.Retrofit;

import com.reryde.provider.Model.CityListModel;
import com.reryde.provider.Model.RatingModel;
import com.reryde.provider.Model.ServiceTypeModel;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.reryde.provider.Helper.URLHelper.RatingApi;

/**
 * Created by CSS on 8/4/2017.
 */

public interface ApiInterface {

    //synchronous.
    @GET("json?")
    Call<ResponseBody> getResponse(@Query("latlng") String param1, @Query("key") String param2);

    @FormUrlEncoded
    @POST("api/provider/trip/{id}/calculate")
    Call<ResponseBody> getLiveTracking(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String strToken,
                                       @Path("id") String id,
                                       @Field("latitude") String latitude, @Field("longitude") String longitude);

    @GET("api/provider/city")
    Call<CityListModel> getCityList(@Header("X-Requested-With") String xmlRequest);

    @GET("api/provider/ratings")
    Call<RatingModel> getRatingList(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String strToken);

    @FormUrlEncoded
    @POST("api/provider/city/service")
    Call<ServiceTypeModel> getServiceList(@Header("X-Requested-With") String xmlRequest, @Field("city_id") String city_id);

    @FormUrlEncoded
    @POST("api/provider/share")
    Call<ResponseBody> sendReferal(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String Authorization, @FieldMap() Map<String, String> partMap , @Field("type") String user, @Field("device_type") String android);

}
