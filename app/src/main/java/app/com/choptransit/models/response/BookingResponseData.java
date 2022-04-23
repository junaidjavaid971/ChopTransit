package app.com.choptransit.models.response;

import androidx.databinding.BaseObservable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class BookingResponseData extends BaseResponse implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("passenger")
    @Expose
    private PassengerResponse.PassengerData passenger;
    @SerializedName("assignedRoute")
    @Expose
    private AssignRouteData assignedRoute;
    @SerializedName("stopName")
    @Expose
    private String stopName;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("status")
    @Expose
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PassengerResponse.PassengerData getPassenger() {
        return passenger;
    }

    public void setPassenger(PassengerResponse.PassengerData passenger) {
        this.passenger = passenger;
    }

    public AssignRouteData getAssignedRoute() {
        return assignedRoute;
    }

    public void setAssignedRoute(AssignRouteData assignedRoute) {
        this.assignedRoute = assignedRoute;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }
}
