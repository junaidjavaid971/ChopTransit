package app.com.choptransit.models.request;

import com.google.gson.annotations.SerializedName;

public class DriverRequestModel {

    @SerializedName("lvl")
    public int lvl;
    @SerializedName("phoneNumber")
    public String phoneNumber;
    @SerializedName("id")
    public String id;
    @SerializedName("firstName")
    public String firstName;
    @SerializedName("lastName")
    public String lastName;
    @SerializedName("email")
    public String email;
    @SerializedName("companyID")
    public String companyID;
    @SerializedName("latitude")
    public String latitude;
    @SerializedName("longitude")
    public String longitude;

    public DriverRequestModel(int lvl, String firstName, String lastName, String email, String phoneNumber, String companyID) {
        this.lvl = lvl;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.companyID = companyID;
    }

    public DriverRequestModel(int lvl) {
        this.lvl = lvl;
    }

    public DriverRequestModel(int lvl, String id) {
        this.id = id;
        this.lvl = lvl;
    }

    public DriverRequestModel(int lvl, String id, String latitude, String longitude) {
        this.id = id;
        this.lvl = lvl;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
