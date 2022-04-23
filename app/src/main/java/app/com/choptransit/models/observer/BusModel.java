package app.com.choptransit.models.observer;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.ArrayList;

import app.com.choptransit.BR;
import app.com.choptransit.models.response.Stop;

public class BusModel extends BaseObservable {
    private String busName = "";
    private String busRegistrationNumber = "";
    private String busColor = "";
    private String busType = "";
    private String id = "";
    private String vacancy = "";
    public boolean updateBus = false;

    @Bindable
    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
        notifyPropertyChanged(BR.busName);
    }

    @Bindable
    public String getBusRegistrationNumber() {
        return busRegistrationNumber;
    }

    public void setBusRegistrationNumber(String busRegistrationNumber) {
        this.busRegistrationNumber = busRegistrationNumber;
        notifyPropertyChanged(BR.busRegistrationNumber);
    }

    @Bindable
    public String getBusColor() {
        return busColor;
    }

    public void setBusColor(String busColor) {
        this.busColor = busColor;
        notifyPropertyChanged(BR.busColor);
    }

    @Bindable
    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
        notifyPropertyChanged(BR.busType);
    }

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getVacancy() {
        return vacancy;
    }

    public void setVacancy(String vacancy) {
        this.vacancy = vacancy;
        notifyPropertyChanged(BR.vacancy);
    }
}
