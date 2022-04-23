package app.com.choptransit.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompanyReqModel {

    @SerializedName("lvl")
    @Expose
    private Integer lvl;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("companyName")
    @Expose
    private String companyName;
    @SerializedName("contactNumber")
    @Expose
    private String contactNumber;
    @SerializedName("email")
    @Expose
    private String email;

    public Integer getLvl() {
        return lvl;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
