package app.com.choptransit.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import app.com.choptransit.R;
import app.com.choptransit.models.observer.DriverSignupModel;
import app.com.choptransit.models.observer.LoginModel;
import app.com.choptransit.models.observer.PessengerSignupModel;
import app.com.choptransit.models.request.DriverRequestModel;
import app.com.choptransit.models.request.LoginRequestModel;
import app.com.choptransit.models.request.PessengerSignupRequestModel;
import app.com.choptransit.models.request.RequestModel;
import app.com.choptransit.models.response.CompanyResponse;
import app.com.choptransit.models.response.DriverRegistrationResponse;
import app.com.choptransit.models.response.PassengerResponse;
import app.com.choptransit.models.response.SendOtpResponse;
import app.com.choptransit.repositories.LoginRepository;

public class AuthViewModel extends AndroidViewModel {

    public LoginModel loginModel = new LoginModel();
    public PessengerSignupModel pessengerSignupModel = new PessengerSignupModel();
    public DriverSignupModel driverRegistrationModel = new DriverSignupModel();
    public MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> dialogLiveData = new MutableLiveData<>();
    public MutableLiveData<PassengerResponse> passengerResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<DriverRegistrationResponse> driverResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<SendOtpResponse> sendOtpResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<PassengerResponse> verifyOtpLiveData = new MutableLiveData<>();
    public MutableLiveData<DriverRegistrationResponse> verifyDriverOtpLiveData = new MutableLiveData<>();
    public MutableLiveData<CompanyResponse.CompanyData> companySelectedLiveData = new MutableLiveData<>();


    LoginRepository repository = new LoginRepository();

    public AuthViewModel(@NonNull Application application) {
        super(application);
    }

    public void validatePhoneNumber() {
        if (loginModel.phoneNumber.isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterPhoneNumber));
        } else {
            dialogLiveData.postValue(true);
            sendOtpRequest();
        }
    }

    public void validatePassengerInformation() {
        if (pessengerSignupModel.getName().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterFullName));
        } else if (pessengerSignupModel.getEmail().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterEmail));
        } else if (pessengerSignupModel.getPhoneNumber().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterPhoneNumber));
        } else {
            dialogLiveData.postValue(true);
            sendRegisterPassengerRequest();
        }
    }


    public void validateDriverInformation() {
        if (driverRegistrationModel.getFirstName().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterFirstName));
        } else if (driverRegistrationModel.getLastName().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterLastName));
        } else if (driverRegistrationModel.getEmail().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterContactEmailValidation));
        } else if (driverRegistrationModel.getPhoneNumber().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterContactNumberValidation));
        } else if (driverRegistrationModel.getCompanyID().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_selectYourCompanyValidation));
        } else {
            dialogLiveData.postValue(true);
            sendRegisterDriverRequest();
        }
    }

    private void sendOtpRequest() {
        LoginRequestModel requestModel = new LoginRequestModel(1, loginModel.phoneNumber, "");
        RequestModel requestModel1 = new RequestModel();
        requestModel1.setData(requestModel);
        repository.getLoginOTP(requestModel1, body -> {
            dialogLiveData.postValue(false);
            sendOtpResponseLiveData.postValue((SendOtpResponse) body);
        });
    }

    public void verifyOTP() {
        if (loginModel.otp.isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterOTP));
        } else {
            dialogLiveData.postValue(true);
            LoginRequestModel requestModel = new LoginRequestModel(2, loginModel.phoneNumber, loginModel.otp);
            RequestModel requestModel1 = new RequestModel();
            requestModel1.setData(requestModel);
            repository.verifyOtp(requestModel1, body -> {
                dialogLiveData.postValue(false);
                verifyOtpLiveData.postValue((PassengerResponse) body);
            });
        }
    }

    public void verifyDriverOtp() {
        if (loginModel.otp.isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterOTP));
        } else {
            dialogLiveData.postValue(true);
            LoginRequestModel requestModel = new LoginRequestModel(5, loginModel.phoneNumber, loginModel.otp);
            RequestModel requestModel1 = new RequestModel();
            requestModel1.setData(requestModel);
            repository.verifyDriverOTP(requestModel1, body -> {
                dialogLiveData.postValue(false);
                verifyDriverOtpLiveData.postValue((DriverRegistrationResponse) body);
            });
        }
    }

    private void sendRegisterPassengerRequest() {
        PessengerSignupRequestModel pessengerSignupRequestModel = new PessengerSignupRequestModel(3, pessengerSignupModel.name, pessengerSignupModel.email, pessengerSignupModel.phoneNumber);
        RequestModel requestModel = new RequestModel();
        requestModel.setData(pessengerSignupRequestModel);
        repository.registerPassenger(requestModel, body -> {
            dialogLiveData.postValue(false);
            passengerResponseLiveData.postValue((PassengerResponse) body);
        });
    }


    private void sendRegisterDriverRequest() {
        DriverRequestModel driverRequestModel = new DriverRequestModel(1, driverRegistrationModel.getFirstName(), driverRegistrationModel.getLastName(), driverRegistrationModel.getEmail(), driverRegistrationModel.getPhoneNumber(), driverRegistrationModel.getCompanyID());
        RequestModel requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);
        repository.registerDriver(requestModel, body -> {
            dialogLiveData.postValue(false);
            driverResponseLiveData.postValue((DriverRegistrationResponse) body);
        });
    }

}
