package app.com.choptransit.repositories;

import app.com.choptransit.models.request.CardRequestModel;
import app.com.choptransit.models.request.PessengerSignupRequestModel;
import app.com.choptransit.models.request.RequestModel;
import app.com.choptransit.models.request.TokenRequest;

public class PassengerRepository extends BaseRepository {

    public void card(RequestModel<CardRequestModel> requestModel, ResponseCallback callback) {
        registerApiRequest(apiService.cards(requestModel), callback);
    }

    public void passenger(RequestModel<PessengerSignupRequestModel> requestModel, ResponseCallback callback) {
        registerApiRequest(apiService.getPassengerInfo(requestModel), callback);
    }

    public void uploadToken(RequestModel<TokenRequest> requestModel, ResponseCallback callback) {
        registerApiRequest(apiService.token(requestModel), callback);
    }
}
