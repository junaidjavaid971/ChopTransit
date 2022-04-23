package app.com.choptransit.models.observer;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import app.com.choptransit.BR;

public class CardObserverModel extends BaseObservable {

    public String cardHolderName = "";
    public String cardNumber = "";
    public String expiry = "";
    public String cvc = "";

    @Bindable
    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
        notifyPropertyChanged(BR.cardHolderName);
    }

    @Bindable
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        notifyPropertyChanged(BR.cardNumber);
    }

    @Bindable
    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
        notifyPropertyChanged(BR.expiry);
    }

    @Bindable
    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
        notifyPropertyChanged(BR.cvc);
    }
}
