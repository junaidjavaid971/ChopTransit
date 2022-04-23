package app.com.choptransit.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DriverData implements Serializable {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("firstName")
    @Expose
    public String firstName;
    @SerializedName("lastName")
    @Expose
    public String lastName;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("userID")
    @Expose
    public String userID;
    @SerializedName("phoneNumber")
    @Expose
    public String phoneNumber;
    @SerializedName("companyID")
    @Expose
    public String companyID;
    @SerializedName("isApproved")
    @Expose
    public String isApproved;
    @SerializedName("companyName")
    @Expose
    public String companyName;

    @SerializedName("signedIn")
    @Expose
    public String signedIn;

    @SerializedName("busName")
    @Expose
    public String busName = "";

    @SerializedName("busRegNo")
    @Expose
    public String busRegNo = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(String isApproved) {
        this.isApproved = isApproved;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSignedIn() {
        return signedIn;
    }

    public void setSignedIn(String signedIn) {
        this.signedIn = signedIn;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusRegNo() {
        return busRegNo;
    }

    public void setBusRegNo(String busRegNo) {
        this.busRegNo = busRegNo;
    }
}