package app.com.choptransit.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AssignRouteData extends BaseResponse implements Serializable {

    @SerializedName("route")
    @Expose
    private RouteData route;
    @SerializedName("bus")
    @Expose
    private BusResponse.BusData bus;
    @SerializedName("departureTime")
    @Expose
    private String departureTime;
    @SerializedName("id")
    @Expose
    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
