package app.com.choptransit.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import app.com.choptransit.models.observer.LoginModel;
import app.com.choptransit.models.request.BookRideRequestModel;
import app.com.choptransit.models.request.DriverRequestModel;
import app.com.choptransit.models.request.RequestModel;
import app.com.choptransit.models.response.BaseResponse;
import app.com.choptransit.models.response.BookingResponse;
import app.com.choptransit.models.response.DriverRegistrationResponse;
import app.com.choptransit.models.response.DriversResponse;
import app.com.choptransit.repositories.BookingRepository;
import app.com.choptransit.repositories.DriverRepository;

public class DriverViewModel extends AndroidViewModel {

    public LoginModel loginModel = new LoginModel();
    DriverRepository repository = new DriverRepository();
    BookingRepository bookingRepository = new BookingRepository();
    public MutableLiveData<Boolean> dialogLiveData = new MutableLiveData<>();
    public MutableLiveData<DriversResponse> driversResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<DriverRegistrationResponse> driverProfileLiveData = new MutableLiveData<>();
    public MutableLiveData<DriversResponse> driverDeletedLiveData = new MutableLiveData<>();
    public MutableLiveData<BaseResponse> baseResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<BaseResponse> locationLiveData = new MutableLiveData<>();
    public MutableLiveData<BaseResponse> pickPassengerLiveData = new MutableLiveData<>();
    public MutableLiveData<BaseResponse> missPassengerLiveData = new MutableLiveData<>();
    public MutableLiveData<BaseResponse> endRideLiveData = new MutableLiveData<>();
    public MutableLiveData<BaseResponse> checkSignInLiveData = new MutableLiveData<>();
    public MutableLiveData<BookingResponse> bookingResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<BookingResponse> confirmedBookingResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<BookingResponse> activeBookingResponseLiveData = new MutableLiveData<>();

    public DriverViewModel(@NonNull Application application) {
        super(application);
    }

    public void approveDriver(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(4, id);
        RequestModel requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.drivers(requestModel, body -> {
            dialogLiveData.postValue(false);
            driversResponseLiveData.postValue((DriversResponse) body);
        });
    }

    public void getDriverProfile(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(2, id);
        RequestModel requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        repository.getDriverProfile(requestModel, body -> {
            driverProfileLiveData.postValue((DriverRegistrationResponse) body);
        });
    }

    public void rejectDriver(@NotNull String id, boolean deleteReq) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(5, id);
        RequestModel requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.drivers(requestModel, body -> {
            dialogLiveData.postValue(false);
            if (deleteReq) {
                driverDeletedLiveData.postValue((DriversResponse) body);
            } else {
                driversResponseLiveData.postValue((DriversResponse) body);
            }
        });
    }

    public void deleteDriver(@NotNull String id, boolean deleteReq) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(27, id);
        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.drivers(requestModel, body -> {
            dialogLiveData.postValue(false);
            if (deleteReq) {
                driverDeletedLiveData.postValue((DriversResponse) body);
            } else {
                driversResponseLiveData.postValue((DriversResponse) body);
            }
        });
    }

    public void signIn(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(9, id);
        RequestModel requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.drivers(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((BaseResponse) body);
        });
    }

    public void checkSignIn(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(11, id);
        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.drivers(requestModel, body -> {
            dialogLiveData.postValue(false);
            checkSignInLiveData.postValue((BaseResponse) body);
        });
    }

    public void signOff(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(10, id);
        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.drivers(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((BaseResponse) body);
        });
    }

    public void getBookingRequests(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(12, id);
        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.driverBookings(requestModel, body -> {
            dialogLiveData.postValue(false);
            bookingResponseLiveData.postValue((BookingResponse) body);
        });
    }

    public void getActiveOrConfirmedRides(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(14, id);
        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.driverBookings(requestModel, body -> {
            dialogLiveData.postValue(false);
            bookingResponseLiveData.postValue((BookingResponse) body);
        });
    }

    public void approveBooking(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(16, id);
        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.driverBookings(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((BookingResponse) body);
        });
    }

    public void pickPassenger(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(17, id);
        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.driverBookings(requestModel, body -> {
            dialogLiveData.postValue(false);
            pickPassengerLiveData.postValue((BookingResponse) body);
        });
    }

    public void missPassenger(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(20, id);
        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.driverBookings(requestModel, body -> {
            dialogLiveData.postValue(false);
            missPassengerLiveData.postValue((BookingResponse) body);
        });
    }

    public void finishRide(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(18, id);
        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.driverBookings(requestModel, body -> {
            dialogLiveData.postValue(false);
            endRideLiveData.postValue((BookingResponse) body);
        });
    }

    public void rejectBooking(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(19, id);
        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.driverBookings(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((BookingResponse) body);
        });
    }

    public void getPendingBookingsForPassenger(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(21, id);
        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.driverBookings(requestModel, body -> {
            dialogLiveData.postValue(false);
            bookingResponseLiveData.postValue((BookingResponse) body);
        });
    }

    public void getConfirmedBookingsForPassenger(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(22, id);
        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.driverBookings(requestModel, body -> {
            dialogLiveData.postValue(false);
            confirmedBookingResponseLiveData.postValue((BookingResponse) body);
        });
    }

    public void getActiveBookingsForPassenger(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(23, id);
        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        dialogLiveData.postValue(true);

        repository.driverBookings(requestModel, body -> {
            dialogLiveData.postValue(false);
            activeBookingResponseLiveData.postValue((BookingResponse) body);
        });
    }

    public void uploadLocation(@NotNull String id, String latitude, String longitude) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(25, id, latitude, longitude);

        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        repository.drivers(requestModel, body -> {
            locationLiveData.postValue((BaseResponse) body);
        });
    }

    public void getDriverLocation(@NotNull String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(26, id);

        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        repository.drivers(requestModel, body -> {
            locationLiveData.postValue((BaseResponse) body);
        });
    }

    public void getPassengerFinishedRides(String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(28, "11");

        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        repository.driverBookings(requestModel, body -> {
            bookingResponseLiveData.postValue((BookingResponse) body);
        });
    }
    public void getDriverFinishedRides(String id) {
        DriverRequestModel driverRequestModel = new DriverRequestModel(15, "21");

        RequestModel<DriverRequestModel> requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);

        repository.driverBookings(requestModel, body -> {
            bookingResponseLiveData.postValue((BookingResponse) body);
        });
    }

}
