package app.com.choptransit.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PassengerResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private PassengerData Data;

    public PassengerData getData() {
        return Data;
    }

    public void setData(PassengerData data) {
        Data = data;
    }

    public class PassengerData implements Serializable {

        @SerializedName("id")
        @Expose
        private String id = "";
        @SerializedName("name")
        @Expose
        private String name = "";
        @SerializedName("email")
        @Expose
        private String email = "";
        @SerializedName("userID")
        @Expose
        private String userID = "";
        @SerializedName("phoneNumber")
        @Expose
        private String phoneNumber = "";
        @SerializedName("customerID")
        @Expose
        private String customerID = "";
        @SerializedName("cardID")
        @Expose
        private String cardID = "";

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public String getCustomerID() {
            return customerID;
        }

        public void setCustomerID(String customerID) {
            this.customerID = customerID;
        }

        public String getCardID() {
            return cardID;
        }

        public void setCardID(String cardID) {
            this.cardID = cardID;
        }
    }
}

