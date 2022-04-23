package app.com.choptransit.models.request;

import com.google.gson.annotations.SerializedName;

public class LoginRequestModel {

    @SerializedName("lvl")
    public int lvl;
    @SerializedName("phoneNumber")
    public String phoneNumber;
    @SerializedName("otp")
    public String otp;

    public LoginRequestModel(int lvl, String phoneNumber, String otp) {
        this.lvl = lvl;
        this.phoneNumber = phoneNumber;
        this.otp = otp;
    }

    public LoginRequestModel() {
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
