package app.com.choptransit.repositories;

import app.com.choptransit.models.request.DriverRequestModel;
import app.com.choptransit.models.request.RequestModel;

public class DriverRepository extends BaseRepository {

    public void drivers(RequestModel<DriverRequestModel> driverRequestModel, ResponseCallback callback) {
        registerApiRequest(apiService.drivers(driverRequestModel), callback);
    }

    public void getDriverProfile(RequestModel<DriverRequestModel> driverRequestModel, ResponseCallback callback) {
        registerApiRequest(apiService.getDriverProfile(driverRequestModel), callback);
    }

    public void driverBookings(RequestModel<DriverRequestModel> driverRequestModel, ResponseCallback callback) {
        registerApiRequest(apiService.driverBookings(driverRequestModel), callback);
    }

}
