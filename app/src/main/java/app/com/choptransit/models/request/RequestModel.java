package app.com.choptransit.models.request;

import com.google.gson.annotations.SerializedName;

public class RequestModel<t> {

    @SerializedName("Data")
    public t Data;

    public t getData() {
        return Data;
    }

    public void setData(t data) {
        Data = data;
    }
}
