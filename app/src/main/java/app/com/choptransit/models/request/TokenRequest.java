package app.com.choptransit.models.request;

import com.google.gson.annotations.SerializedName;

public class TokenRequest {

    @SerializedName("lvl")
    public String lvl;
    @SerializedName("userID")
    public String userID;
    @SerializedName("token")
    public String token;

    public String getLvl() {
        return lvl;
    }

    public void setLvl(String lvl) {
        this.lvl = lvl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
