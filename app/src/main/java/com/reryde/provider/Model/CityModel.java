package com.reryde.provider.Model;

import android.content.Intent;

import java.io.Serializable;

/**
 * Created by Tranxit Technologies Pvt Ltd. on 27-03-2018.
 */

public class CityModel implements Serializable {

    public CityModel(String id ,String cityName){
        this.CityName = cityName;
        this.id = id;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String CityName;
    String id ;
}
