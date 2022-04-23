package app.com.choptransit.repositories;
import app.com.choptransit.models.request.BusReqModel;
import app.com.choptransit.models.request.RequestModel;

public class BusRepository extends BaseRepository {

    public void buses(RequestModel<BusReqModel> requestModel, ResponseCallback callback) {
        registerApiRequest(apiService.buses(requestModel), callback);
    }
}
