package app.com.choptransit.repositories;

import app.com.choptransit.models.request.AdminRequestModel;
import app.com.choptransit.models.request.AssignRouteRequest;
import app.com.choptransit.models.request.RequestModel;

public class AdminRepository extends BaseRepository {

    public void getCounts(RequestModel<AdminRequestModel> requestModel, ResponseCallback callback) {
        registerApiRequest(apiService.admin(requestModel), callback);
    }
}
