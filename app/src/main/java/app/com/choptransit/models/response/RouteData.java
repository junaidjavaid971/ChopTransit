package app.com.choptransit.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class RouteData implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("routeName")
    @Expose
    private String routeName;
    @SerializedName("fare")
    @Expose
    private String fare;
    @SerializedName("stops")
    @Expose
    private ArrayList<Stop> stops = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public void setStops(ArrayList<Stop> stops) {
        this.stops = stops;
    }

}