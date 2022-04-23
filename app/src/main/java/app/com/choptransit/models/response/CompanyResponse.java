package app.com.choptransit.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CompanyResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        @SerializedName("companies")
        private ArrayList<CompanyData> companiesList;

        public ArrayList<CompanyData> getCompaniesList() {
            return companiesList;
        }

        public void setCompaniesList(ArrayList<CompanyData> companiesList) {
            this.companiesList = companiesList;
        }
    }

    public class CompanyData implements Serializable {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("companyName")
        @Expose
        private String companyName;
        @SerializedName("companyContactNumber")
        @Expose
        private String companyContactNumber;
        @SerializedName("companyEmail")
        @Expose
        private String companyEmail;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCompanyContactNumber() {
            return companyContactNumber;
        }

        public void setCompanyContactNumber(String companyContactNumber) {
            this.companyContactNumber = companyContactNumber;
        }

        public String getCompanyEmail() {
            return companyEmail;
        }

        public void setCompanyEmail(String companyEmail) {
            this.companyEmail = companyEmail;
        }
    }
}





