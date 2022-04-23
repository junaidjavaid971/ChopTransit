package app.com.choptransit.models.observer;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.ArrayList;

import app.com.choptransit.BR;
import app.com.choptransit.models.response.Stop;

public class StopModel extends BaseObservable {
    private String stopName = "";
    private double latitude = 0.0;
    private double longitude = 0.0;

    private String id = "";
    public boolean updateStop = false;

    @Bindable
    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
        notifyPropertyChanged(BR.stopName);
    }

    @Bindable
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        notifyPropertyChanged(BR.latitude);
    }

    @Bindable
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        notifyPropertyChanged(BR.longitude);
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
