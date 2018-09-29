package com.reryde.provider.Model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Tranxit Technologies Pvt Ltd. on 27-03-2018.
 */

public class RegisterModel implements Parcelable {
    byte[] UserPTofile,VehiclePhoto,DrivingLincence;

    String strEmailid,strFirstName,strLastname,strCountryCode,strMobileNumber,strVehicleMake,strVehicleModel,strVehicleYear,strVehicleColor,strVehiclNumPlate,strReferral;
    String strService_type;
    String strCityID;


    public String getStrCityID() {
        return strCityID;
    }

    public void setStrCityID(String strCityID) {
        this.strCityID = strCityID;
    }


    public RegisterModel(Parcel in) {
        strEmailid = in.readString();
        strFirstName = in.readString();
        strLastname = in.readString();
        strCountryCode = in.readString();
        strMobileNumber = in.readString();
        strVehicleMake = in.readString();
        strVehicleModel = in.readString();
        strVehicleYear = in.readString();
        strVehicleColor = in.readString();
        strVehiclNumPlate = in.readString();
        strReferral = in.readString();
        strService_type = in.readString();
        strCityID = in.readString();
    }

    public RegisterModel() {
    }

    public static final Creator<RegisterModel> CREATOR = new Creator<RegisterModel>() {
        @Override
        public RegisterModel createFromParcel(Parcel in) {
            return new RegisterModel(in);
        }

        @Override
        public RegisterModel[] newArray(int size) {
            return new RegisterModel[size];
        }
    };

    public byte[] getUserPTofile() {
        return UserPTofile;
    }

    public void setUserPTofile(byte[] userPTofile) {
        UserPTofile = userPTofile;
    }

    public byte[] getVehiclePhoto() {
        return VehiclePhoto;
    }

    public void setVehiclePhoto(byte[] vehiclePhoto) {
        VehiclePhoto = vehiclePhoto;
    }

    public byte[] getDrivingLincence() {
        return DrivingLincence;
    }

    public void setDrivingLincence(byte[] drivingLincence) {
        DrivingLincence = drivingLincence;
    }

    public String getStrEmailid() {
        return strEmailid;
    }

    public void setStrEmailid(String strEmailid) {
        this.strEmailid = strEmailid;
    }

    public String getStrFirstName() {
        return strFirstName;
    }

    public void setStrFirstName(String strFirstName) {
        this.strFirstName = strFirstName;
    }

    public String getStrLastname() {
        return strLastname;
    }

    public void setStrLastname(String strLastname) {
        this.strLastname = strLastname;
    }

    public String getStrCountryCode() {
        return strCountryCode;
    }

    public void setStrCountryCode(String strCountryCode) {
        this.strCountryCode = strCountryCode;
    }

    public String getStrMobileNumber() {
        return strMobileNumber;
    }

    public void setStrMobileNumber(String strMobileNumber) {
        this.strMobileNumber = strMobileNumber;
    }

    public String getStrVehicleMake() {
        return strVehicleMake;
    }

    public void setStrVehicleMake(String strVehicleMake) {
        this.strVehicleMake = strVehicleMake;
    }

    public String getStrVehicleModel() {
        return strVehicleModel;
    }

    public void setStrVehicleModel(String strVehicleModel) {
        this.strVehicleModel = strVehicleModel;
    }

    public String getStrVehicleYear() {
        return strVehicleYear;
    }

    public void setStrVehicleYear(String strVehicleYear) {
        this.strVehicleYear = strVehicleYear;
    }

    public String getStrVehicleColor() {
        return strVehicleColor;
    }

    public void setStrVehicleColor(String strVehicleColor) {
        this.strVehicleColor = strVehicleColor;
    }

    public String getStrVehiclNumPlate() {
        return strVehiclNumPlate;
    }

    public void setStrVehiclNumPlate(String strVehiclNumPlate) {
        this.strVehiclNumPlate = strVehiclNumPlate;
    }

    public String getStrReferral() {
        return strReferral;
    }

    public void setStrReferral(String strReferral) {
        this.strReferral = strReferral;
    }


    public String getStrService_type() {
        return strService_type;
    }

    public void setStrService_type(String strService_type) {
        this.strService_type = strService_type;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strEmailid);
        dest.writeString(strFirstName);
        dest.writeString(strLastname);
        dest.writeString(strCountryCode);
        dest.writeString(strMobileNumber);
        dest.writeString(strVehicleMake);
        dest.writeString(strVehicleModel);
        dest.writeString(strVehicleYear);
        dest.writeString(strVehicleColor);
        dest.writeString(strVehiclNumPlate);
        dest.writeString(strReferral);
        dest.writeString(strService_type);
        dest.writeString(strCityID);
    }
}
