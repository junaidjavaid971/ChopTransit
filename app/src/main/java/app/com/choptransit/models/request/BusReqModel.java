package app.com.choptransit.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import app.com.choptransit.models.response.Stop;

public class BusReqModel {

    @SerializedName("lvl")
    @Expose
    private Integer lvl;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("busName")
    @Expose
    private String busName;
    @SerializedName("registrationNumber")
    @Expose
    private String registrationNumber;
    @SerializedName("busType")
    @Expose
    private String busType;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("vacancy")
    @Expose
    private String vacancy;

    public Integer getLvl() {
        return lvl;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVacancy() {
        return vacancy;
    }

    public void setVacancy(String vacancy) {
        this.vacancy = vacancy;
    }
}
