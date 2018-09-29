
package com.reryde.provider.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceType {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("city_id")
    @Expose
    private Integer cityId;
    @SerializedName("service_type_id")
    @Expose
    private Integer serviceTypeId;
    @SerializedName("fixed")
    @Expose
    private String fixed;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("minute")
    @Expose
    private String minute;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("service")
    @Expose
    private List<Service> service = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getFixed() {
        return fixed;
    }

    public void setFixed(String fixed) {
        this.fixed = fixed;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Service> getService() {
        return service;
    }

    public void setService(List<Service> service) {
        this.service = service;
    }

}
