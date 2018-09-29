
package com.reryde.provider.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Service {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("provider_name")
    @Expose
    private String providerName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("capacity")
    @Expose
    private Integer capacity;
    @SerializedName("fixed")
    @Expose
    private Object fixed;
    @SerializedName("minimum_charge")
    @Expose
    private String minimumCharge;
    @SerializedName("radius")
    @Expose
    private Integer radius;
    @SerializedName("price")
    @Expose
    private Object price;
    @SerializedName("minute")
    @Expose
    private Object minute;
    @SerializedName("distance")
    @Expose
    private Object distance;
    @SerializedName("calculator")
    @Expose
    private String calculator;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("status")
    @Expose
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Object getFixed() {
        return fixed;
    }

    public void setFixed(Object fixed) {
        this.fixed = fixed;
    }

    public String getMinimumCharge() {
        return minimumCharge;
    }

    public void setMinimumCharge(String minimumCharge) {
        this.minimumCharge = minimumCharge;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public Object getPrice() {
        return price;
    }

    public void setPrice(Object price) {
        this.price = price;
    }

    public Object getMinute() {
        return minute;
    }

    public void setMinute(Object minute) {
        this.minute = minute;
    }

    public Object getDistance() {
        return distance;
    }

    public void setDistance(Object distance) {
        this.distance = distance;
    }

    public String getCalculator() {
        return calculator;
    }

    public void setCalculator(String calculator) {
        this.calculator = calculator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
