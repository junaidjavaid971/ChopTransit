package app.com.choptransit.repositories;

import app.com.choptransit.app.com.choptransit.models.request.StopRequestModel;
import app.com.choptransit.models.request.RequestModel;
import app.com.choptransit.models.request.RouteRequestModel;

public class RoutesRepository extends BaseRepository {

    public void addNewRoute(RequestModel<RouteRequestModel> requestModel, ResponseCallback callback) {
        registerApiRequest(apiService.route(requestModel), callback);
    }

    public void getAllRoutes(RequestModel<RouteRequestModel> requestModel, ResponseCallback callback) {
        registerApiRequest(apiService.route(requestModel), callback);
    }

}
