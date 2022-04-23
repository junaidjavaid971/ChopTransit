package app.com.choptransit.repositories;

import app.com.choptransit.app.com.choptransit.models.request.StopRequestModel;
import app.com.choptransit.models.request.RequestModel;

public class StopsRepository extends BaseRepository {

    public void stops(RequestModel<StopRequestModel> requestModel, ResponseCallback callback) {
        registerApiRequest(apiService.stop(requestModel), callback);
    }
}
