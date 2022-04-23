package app.com.choptransit.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DriversResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private ArrayList<DriverData> Data;

    public ArrayList<DriverData> getData() {
        return Data;
    }

    public void setData(ArrayList<DriverData> data) {
        Data = data;
    }

}


