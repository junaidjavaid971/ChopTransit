package app.com.choptransit.repositories;


import android.util.Log;

import app.com.choptransit.models.request.DriverRequestModel;
import app.com.choptransit.models.request.LoginRequestModel;
import app.com.choptransit.models.request.PessengerSignupRequestModel;
import app.com.choptransit.models.request.RequestModel;
import app.com.choptransit.models.response.DriversResponse;
import app.com.choptransit.models.response.PassengerResponse;

public class LoginRepository extends BaseRepository {

    public void getLoginOTP(RequestModel<LoginRequestModel> loginRequestModel, ResponseCallback callback) {
        registerApiRequest(apiService.getLoginOTP(loginRequestModel), callback);
    }

    public void verifyOtp(RequestModel<LoginRequestModel> loginRequestModel, ResponseCallback callback) {
        registerApiRequest(apiService.verifyOTP(loginRequestModel), callback);
    }

    public void drivers(RequestModel<DriverRequestModel> driverRequestModel, ResponseCallback callback) {
        registerApiRequest(apiService.drivers(driverRequestModel), callback);
    }

    public void verifyDriverOTP(RequestModel<LoginRequestModel> loginRequestModel, ResponseCallback callback) {
        registerApiRequest(apiService.verifyDriverOTP(loginRequestModel), callback);
    }

    public void registerPassenger(RequestModel<PessengerSignupRequestModel> loginRequestModel, ResponseCallback callback) {
        registerApiRequest(apiService.registerPassenger(loginRequestModel), callback);
    }

    public void registerDriver(RequestModel<DriverRequestModel> driverReqModel, ResponseCallback callback) {
        registerApiRequest(apiService.registerDriver(driverReqModel), callback);
    }
}
