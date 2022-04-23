package app.com.choptransit.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class AssignRouteResponse extends BaseResponse {
    @SerializedName("data")
    private ArrayList<AssignRouteData> arrayList;

    public ArrayList<AssignRouteData> getData() {
        return arrayList;
    }

    public void setData(ArrayList<AssignRouteData> data) {
        this.arrayList = data;
    }
}

