package app.com.choptransit.models.observer;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import app.com.choptransit.BR;

public class PessengerSignupModel extends BaseObservable {
    public String name = "";
    public String email = "";
    public String phoneNumber = "";

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        notifyPropertyChanged(BR.phoneNumber);
    }
}
