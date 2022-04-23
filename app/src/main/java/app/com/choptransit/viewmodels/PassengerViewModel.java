package app.com.choptransit.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import app.com.choptransit.R;
import app.com.choptransit.models.observer.CardObserverModel;
import app.com.choptransit.models.request.CardRequestModel;
import app.com.choptransit.models.request.PessengerSignupRequestModel;
import app.com.choptransit.models.request.RequestModel;
import app.com.choptransit.models.request.TokenRequest;
import app.com.choptransit.models.response.BaseResponse;
import app.com.choptransit.models.response.CardResponse;
import app.com.choptransit.models.response.PassengerResponse;
import app.com.choptransit.repositories.PassengerRepository;
import app.com.choptransit.utilities.CreditCardType;

public class PassengerViewModel extends AndroidViewModel {

    public CardObserverModel cardObserverModel = new CardObserverModel();

    public MutableLiveData<Integer> errorLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> dialogLiveData = new MutableLiveData<>();
    public MutableLiveData<CardResponse> saveCardLiveData = new MutableLiveData<>();
    public MutableLiveData<CardResponse> cardLiveData = new MutableLiveData<>();
    public MutableLiveData<BaseResponse> baseResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<CardResponse> transactionLiveData = new MutableLiveData<>();
    public MutableLiveData<PassengerResponse> passengerResponseLiveData = new MutableLiveData<>();

    PassengerRepository repository = new PassengerRepository();

    public PassengerViewModel(@NonNull Application application) {
        super(application);
    }

    public void validateCardInformation() {
        if (cardObserverModel.getCardHolderName().isEmpty()) {
            errorLiveData.postValue(R.id.etCardHolderName);
        } else if (cardObserverModel.getCardNumber().isEmpty() || CreditCardType.detect(cardObserverModel.getCardNumber()) == null) {
            errorLiveData.postValue(R.id.etCardNumber);
        } else if (cardObserverModel.getExpiry().isEmpty() || cardObserverModel.getExpiry().length() < 5) {
            errorLiveData.postValue(R.id.etExpiry);
        } else if (cardObserverModel.getCvc().isEmpty() || cardObserverModel.getCvc().length() < 3) {
            errorLiveData.postValue(R.id.etCVC);
        } else {
            errorLiveData.postValue(-1);
        }
    }

    public void saveCardDetails(CardObserverModel cardDetails, String id, String email) {
        String[] expiry = cardDetails.getExpiry().split("/");

        CardRequestModel cardRequestModel = new CardRequestModel();
        cardRequestModel.setLvl(1);
        cardRequestModel.setId(id);
        cardRequestModel.setCardNumber(cardDetails.getCardNumber().replace("-", ""));
        cardRequestModel.setCardHolderName(cardDetails.getCardHolderName());
        cardRequestModel.setExpiryMonth(expiry[0]);
        cardRequestModel.setExpiryYear(expiry[1]);
        cardRequestModel.setCvc(cardDetails.getCvc());
        cardRequestModel.setEmail(email);

        dialogLiveData.postValue(true);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(cardRequestModel);

        repository.card(requestModel, body -> {
            dialogLiveData.postValue(false);
            saveCardLiveData.postValue((CardResponse) body);
        });
    }

    public void getCardDetails(String customerID, String cardID) {
        CardRequestModel cardRequestModel = new CardRequestModel();
        cardRequestModel.setLvl(4);
        cardRequestModel.setCustomerID(customerID);
        cardRequestModel.setCardID(cardID);

        dialogLiveData.postValue(true);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(cardRequestModel);

        repository.card(requestModel, body -> {
            dialogLiveData.postValue(false);
            cardLiveData.postValue((CardResponse) body);
        });
    }

    public void getPassengerInfo(String id, int lvl) {
        PessengerSignupRequestModel signupRequestModel = new PessengerSignupRequestModel(lvl, id);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(signupRequestModel);

        dialogLiveData.postValue(true);

        repository.passenger(requestModel, body -> {
            dialogLiveData.postValue(false);
            passengerResponseLiveData.postValue((PassengerResponse) body);
        });
    }

    public void deleteCard(@Nullable String customerID, @Nullable String cardID, @NotNull String id) {
        CardRequestModel cardRequestModel = new CardRequestModel();
        cardRequestModel.setLvl(7);
        cardRequestModel.setCustomerID(customerID);
        cardRequestModel.setCardID(cardID);
        cardRequestModel.setId(id);

        dialogLiveData.postValue(true);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(cardRequestModel);

        repository.card(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((CardResponse) body);
        });
    }

    public void performTransaction(String customerID, String amount, String id) {
        CardRequestModel cardRequestModel = new CardRequestModel();
        cardRequestModel.setLvl(2);
        cardRequestModel.setCustomerID(customerID);
        cardRequestModel.setId(id);
        cardRequestModel.setAmount(amount);

        dialogLiveData.postValue(true);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(cardRequestModel);

        repository.card(requestModel, body -> {
            dialogLiveData.postValue(false);
            transactionLiveData.postValue((CardResponse) body);
        });
    }

    public void savePassengerToken(String token, String userID) {
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setLvl("1");
        tokenRequest.setUserID(userID);
        tokenRequest.setToken(token);

        RequestModel<TokenRequest> requestModel = new RequestModel();
        requestModel.setData(tokenRequest);

        repository.uploadToken(requestModel, body -> {
            baseResponseLiveData.postValue((BaseResponse) body);
        });
    }
}
