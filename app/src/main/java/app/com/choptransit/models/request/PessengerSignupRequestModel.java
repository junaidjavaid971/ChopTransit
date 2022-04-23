package app.com.choptransit.models.request;

import com.google.gson.annotations.SerializedName;

public class PessengerSignupRequestModel {

    @SerializedName("lvl")
    public int lvl;
    @SerializedName("id")
    public String id;
    @SerializedName("phoneNumber")
    public String phoneNumber;
    @SerializedName("name")
    public String name;
    @SerializedName("email")
    public String email;

    public PessengerSignupRequestModel(int lvl, String name, String email, String phoneNumber) {
        this.lvl = lvl;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.email = email;
    }

    public PessengerSignupRequestModel(int lvl, String id) {
        this.id = id;
        this.lvl = lvl;
    }
}
