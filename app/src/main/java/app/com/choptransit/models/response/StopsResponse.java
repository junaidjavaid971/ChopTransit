package app.com.choptransit.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class StopsResponse extends BaseResponse {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        @SerializedName("stops")
        private ArrayList<Stop> stopsList;

        public ArrayList<Stop> getStopsList() {
            return stopsList;
        }

        public void setData(ArrayList<Stop> stopsList) {
            this.stopsList = stopsList;
        }
    }
}

