package app.com.choptransit.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CardResponse extends BaseResponse {

    @SerializedName("data")
    private CardData data;

    public CardData getData() {
        return data;
    }

    public void setData(CardData data) {
        this.data = data;
    }

    public class CardData implements Serializable {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("brand")
        @Expose
        private String brand;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("expiryMonth")
        @Expose
        private Object expiryMonth;
        @SerializedName("customer")
        @Expose
        private String customer;
        @SerializedName("cvc_check")
        @Expose
        private String cvcCheck;
        @SerializedName("last4")
        @Expose
        private String last4;
        @SerializedName("funding")
        @Expose
        private String funding;
        @SerializedName("exp_year")
        @Expose
        private Integer expYear;
        @SerializedName("exp_month")
        @Expose
        private Integer expMonth;
        @SerializedName("fingerprint")
        @Expose
        private String fingerprint;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public Object getExpiryMonth() {
            return expiryMonth;
        }

        public void setExpiryMonth(Object expiryMonth) {
            this.expiryMonth = expiryMonth;
        }

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }

        public String getCvcCheck() {
            return cvcCheck;
        }

        public void setCvcCheck(String cvcCheck) {
            this.cvcCheck = cvcCheck;
        }

        public String getLast4() {
            return last4;
        }

        public void setLast4(String last4) {
            this.last4 = last4;
        }

        public String getFunding() {
            return funding;
        }

        public void setFunding(String funding) {
            this.funding = funding;
        }

        public Integer getExpYear() {
            return expYear;
        }

        public void setExpYear(Integer expYear) {
            this.expYear = expYear;
        }

        public Integer getExpMonth() {
            return expMonth;
        }

        public void setExpMonth(Integer expMonth) {
            this.expMonth = expMonth;
        }

        public String getFingerprint() {
            return fingerprint;
        }

        public void setFingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
        }

    }


}