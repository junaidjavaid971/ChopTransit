package app.com.choptransit.repositories;

import app.com.choptransit.models.request.AssignRouteRequest;
import app.com.choptransit.models.request.BookRideRequestModel;
import app.com.choptransit.models.request.RequestModel;

public class BookingRepository extends BaseRepository {

    public void booking(RequestModel<BookRideRequestModel> requestModel, ResponseCallback callback) {
        registerApiRequest(apiService.bookings(requestModel), callback);
    }
}
