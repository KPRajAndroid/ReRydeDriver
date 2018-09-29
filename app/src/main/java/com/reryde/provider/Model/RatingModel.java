package com.reryde.provider.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Tranxit Technologies Pvt Ltd. on 30-03-2018.
 */

public class RatingModel implements Serializable {
    @SerializedName("life_time_trips")
    @Expose
    private Integer lifeTimeTrips;
    @SerializedName("rated_trips")
    @Expose
    private Integer ratedTrips;
    @SerializedName("five_rates")
    @Expose
    private Integer fiveRates;
    @SerializedName("current_ratings")
    @Expose
    private double currentRatings;

    public Integer getLifeTimeTrips() {
        return lifeTimeTrips;
    }

    public void setLifeTimeTrips(Integer lifeTimeTrips) {
        this.lifeTimeTrips = lifeTimeTrips;
    }

    public Integer getRatedTrips() {
        return ratedTrips;
    }

    public void setRatedTrips(Integer ratedTrips) {
        this.ratedTrips = ratedTrips;
    }

    public Integer getFiveRates() {
        return fiveRates;
    }

    public void setFiveRates(Integer fiveRates) {
        this.fiveRates = fiveRates;
    }

    public double getCurrentRatings() {
        return currentRatings;
    }

    public void setCurrentRatings(double currentRatings) {
        this.currentRatings = currentRatings;
    }

}
