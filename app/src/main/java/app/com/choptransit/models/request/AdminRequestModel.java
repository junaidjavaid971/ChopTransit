package app.com.choptransit.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import app.com.choptransit.models.response.BusResponse;
import app.com.choptransit.models.response.RouteData;

public class AdminRequestModel {

    @SerializedName("lvl")
    @Expose
    private Integer lvl;

    public Integer getLvl() {
        return lvl;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }
}
