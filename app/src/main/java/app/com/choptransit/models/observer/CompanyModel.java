package app.com.choptransit.models.observer;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import app.com.choptransit.BR;

public class CompanyModel extends BaseObservable {
    private String companyName = "";
    private String companyContactNumber = "";
    private String companyEmail = "";
    private String id = "";

    public boolean updateCompany = false;

    @Bindable
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
        notifyPropertyChanged(BR.companyName);
    }

    @Bindable
    public String getCompanyContactNumber() {
        return companyContactNumber;
    }

    public void setCompanyContactNumber(String companyContactNumber) {
        this.companyContactNumber = companyContactNumber;
        notifyPropertyChanged(BR.companyContactNumber);
    }

    @Bindable
    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
        notifyPropertyChanged(BR.companyEmail);
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
