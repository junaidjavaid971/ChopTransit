package app.com.choptransit.models.observer;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import app.com.choptransit.BR;

public class LoginModel extends BaseObservable {
    public String otp = "";
    public String phoneNumber = "";

    @Bindable
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
        notifyPropertyChanged(BR.otp);
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
