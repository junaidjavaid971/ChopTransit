package app.com.choptransit.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import app.com.choptransit.models.response.BusResponse;
import app.com.choptransit.models.response.RouteData;

public class AssignRouteRequest {

    @SerializedName("lvl")
    @Expose
    private Integer lvl;
    @SerializedName("route")
    @Expose
    private RouteData route;
    @SerializedName("bus")
    @Expose
    private BusResponse.BusData bus;
    @SerializedName("departureTime")
    @Expose
    private String departureTime;
    @SerializedName("busID")
    @Expose
    private String busID;
    @SerializedName("driverID")
    @Expose
    private String driverID;

    public Integer getLvl() {
        return lvl;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public RouteData getRoute() {
        return route;
    }

    public void setRoute(RouteData route) {
        this.route = route;
    }

    public BusResponse.BusData getBus() {
        return bus;
    }

    public void setBus(BusResponse.BusData bus) {
        this.bus = bus;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getBusID() {
        return busID;
    }

    public void setBusID(String busID) {
        this.busID = busID;
    }
}
