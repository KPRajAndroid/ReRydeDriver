
package com.reryde.provider.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceTypeModel {

    @SerializedName("service_type")
    @Expose
    private List<ServiceType> serviceType = null;

    public List<ServiceType> getServiceType() {
        return serviceType;
    }

    public void setServiceType(List<ServiceType> serviceType) {
        this.serviceType = serviceType;
    }

}
