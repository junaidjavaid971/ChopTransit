package app.com.choptransit.repositories;

import app.com.choptransit.models.request.AssignRouteRequest;
import app.com.choptransit.models.request.BusReqModel;
import app.com.choptransit.models.request.RequestModel;

public class AssignRouteRepository extends BaseRepository {

    public void assignRoutes(RequestModel<AssignRouteRequest> requestModel, ResponseCallback callback) {
        registerApiRequest(apiService.assignRoutes(requestModel), callback);
    }
}
