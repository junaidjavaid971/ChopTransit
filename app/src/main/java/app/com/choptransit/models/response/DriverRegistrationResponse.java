package app.com.choptransit.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverRegistrationResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private DriverData Data;

    public DriverData getData() {
        return Data;
    }

    public void setData(DriverData data) {
        Data = data;
    }


}


