package app.com.choptransit.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CardRequestModel {

    @SerializedName("lvl")
    @Expose
    private Integer lvl;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("cardNumber")
    @Expose
    private String cardNumber;
    @SerializedName("expiryMonth")
    @Expose
    private String expiryMonth;
    @SerializedName("expiryYear")
    @Expose
    private String expiryYear;
    @SerializedName("cvc")
    @Expose
    private String cvc;
    @SerializedName("cardHolderName")
    @Expose
    private String cardHolderName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("customerID")
    @Expose
    private String customerID;
    @SerializedName("cardID")
    @Expose
    private String cardID;
    @SerializedName("amount")
    @Expose
    private String amount;

    public Integer getLvl() {
        return lvl;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
