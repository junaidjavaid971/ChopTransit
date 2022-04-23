package app.com.choptransit.repositories;

import app.com.choptransit.app.com.choptransit.models.request.StopRequestModel;
import app.com.choptransit.models.request.CompanyReqModel;
import app.com.choptransit.models.request.RequestModel;

public class CompanyRepository extends BaseRepository {

    public void companies(RequestModel<CompanyReqModel> requestModel, ResponseCallback callback) {
        registerApiRequest(apiService.company(requestModel), callback);
    }
}
