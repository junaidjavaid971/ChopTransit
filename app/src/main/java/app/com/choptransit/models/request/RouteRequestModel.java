package app.com.choptransit.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import app.com.choptransit.models.response.Stop;

public class RouteRequestModel {

    @SerializedName("lvl")
    @Expose
    private Integer lvl;
    @SerializedName("routeName")
    @Expose
    private String routeName;
    @SerializedName("fare")
    @Expose
    private Integer fare;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("companyId")
    @Expose
    private Integer companyId;
    @SerializedName("stops")
    @Expose
    private ArrayList<Stop> stops = null;

    public Integer getLvl() {
        return lvl;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Integer getFare() {
        return fare;
    }

    public void setFare(Integer fare) {
        this.fare = fare;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public void setStops(ArrayList<Stop> stops) {
        this.stops = stops;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
