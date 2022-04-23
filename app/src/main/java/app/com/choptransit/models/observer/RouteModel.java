package app.com.choptransit.models.observer;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.ArrayList;

import app.com.choptransit.BR;
import app.com.choptransit.models.response.Stop;

public class RouteModel extends BaseObservable {
    private String routeName = "";
    private String fare = "";
    private ArrayList<Stop> stopsList = new ArrayList<>();
    private String id;

    public boolean updateRoute = false;

    @Bindable
    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
        notifyPropertyChanged(BR.routeName);
    }

    @Bindable
    public String getFare() {
        return fare;
    }

    @Bindable
    public void setFare(String fare) {
        this.fare = fare;
    }

    public ArrayList<Stop> getStopsList() {
        return stopsList;
    }

    public void setStopsList(ArrayList<Stop> stopsList) {
        this.stopsList = stopsList;
    }

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }
}
