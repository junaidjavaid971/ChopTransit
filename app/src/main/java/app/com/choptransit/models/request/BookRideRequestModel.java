package app.com.choptransit.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import app.com.choptransit.models.response.BusResponse;
import app.com.choptransit.models.response.RouteData;

public class BookRideRequestModel {

    @SerializedName("lvl")
    @Expose
    private Integer lvl;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("passengerID")
    @Expose
    private String passengerID;
    @SerializedName("driverID")
    @Expose
    private String driverID;
    @SerializedName("assignedRouteID")
    @Expose
    private String assignedRouteID;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("stopName")
    @Expose
    private String stopName;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;

    public Integer getLvl() {
        return lvl;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public String getPassengerID() {
        return passengerID;
    }

    public void setPassengerID(String passengerID) {
        this.passengerID = passengerID;
    }

    public String getAssignedRouteID() {
        return assignedRouteID;
    }

    public void setAssignedRouteID(String assignedRouteID) {
        this.assignedRouteID = assignedRouteID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }
}
