package app.com.choptransit.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import okhttp3.Route;

public class RouteResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        @SerializedName("routes")
        private ArrayList<RouteData> routesList;

        public ArrayList<RouteData> getRoutesList() {
            return routesList;
        }

        public void setData(ArrayList<RouteData> routesList) {
            this.routesList = routesList;
        }
    }
}





