package app.com.choptransit.network;

import app.com.choptransit.app.com.choptransit.models.request.StopRequestModel;
import app.com.choptransit.models.request.AdminRequestModel;
import app.com.choptransit.models.request.AssignRouteRequest;
import app.com.choptransit.models.request.BookRideRequestModel;
import app.com.choptransit.models.request.BusReqModel;
import app.com.choptransit.models.request.CompanyReqModel;
import app.com.choptransit.models.request.DriverRequestModel;
import app.com.choptransit.models.request.LoginRequestModel;
import app.com.choptransit.models.request.CardRequestModel;
import app.com.choptransit.models.request.PessengerSignupRequestModel;
import app.com.choptransit.models.request.RequestModel;
import app.com.choptransit.models.request.RouteRequestModel;
import app.com.choptransit.models.request.TokenRequest;
import app.com.choptransit.models.response.AssignRouteResponse;
import app.com.choptransit.models.response.BaseResponse;
import app.com.choptransit.models.response.BookingResponse;
import app.com.choptransit.models.response.BusResponse;
import app.com.choptransit.models.response.CardResponse;
import app.com.choptransit.models.response.CompanyResponse;
import app.com.choptransit.models.response.DriverData;
import app.com.choptransit.models.response.DriverRegistrationResponse;
import app.com.choptransit.models.response.DriversResponse;
import app.com.choptransit.models.response.PassengerResponse;
import app.com.choptransit.models.response.RouteResponse;
import app.com.choptransit.models.response.SendOtpResponse;
import app.com.choptransit.models.response.StopsResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GetDataService {
    @POST("auth.php")
    Call<SendOtpResponse> getLoginOTP(@Body RequestModel<LoginRequestModel> requestModel);

    @POST("auth.php")
    Call<PassengerResponse> verifyOTP(@Body RequestModel<LoginRequestModel> requestModel);

    @POST("auth.php")
    Call<DriverRegistrationResponse> verifyDriverOTP(@Body RequestModel<LoginRequestModel> requestModel);

    @POST("auth.php")
    Call<PassengerResponse> registerPassenger(@Body RequestModel<PessengerSignupRequestModel> requestModel);

    @POST("auth.php")
    Call<PassengerResponse> getPassengerInfo(@Body RequestModel<PessengerSignupRequestModel> requestModel);

    @POST("token.php")
    Call<BaseResponse> token(@Body RequestModel<TokenRequest> requestModel);

    @POST("drivers.php")
    Call<DriverRegistrationResponse> registerDriver(@Body RequestModel<DriverRequestModel> requestModel);

    @POST("drivers.php")
    Call<DriversResponse> drivers(@Body RequestModel<DriverRequestModel> requestModel);

    @POST("drivers.php")
    Call<DriverRegistrationResponse> getDriverProfile(@Body RequestModel<DriverRequestModel> requestModel);

    @POST("drivers.php")
    Call<BookingResponse> driverBookings(@Body RequestModel<DriverRequestModel> requestModel);

    @POST("routes.php")
    Call<StopsResponse> stop(@Body RequestModel<StopRequestModel> stopRequestModelRequestModel);

    @POST("company.php")
    Call<CompanyResponse> company(@Body RequestModel<CompanyReqModel> stopRequestModelRequestModel);

    @POST("routes.php")
    Call<RouteResponse> route(@Body RequestModel<RouteRequestModel> routeRequestModelRequestModel);

    @POST("buses.php")
    Call<BusResponse> buses(@Body RequestModel<BusReqModel> busRequestModelRequestModel);

    @POST("assignroute.php")
    Call<AssignRouteResponse> assignRoutes(@Body RequestModel<AssignRouteRequest> assignRouteRequestRequestModel);

    @POST("admin.php")
    Call<BaseResponse> admin(@Body RequestModel<AdminRequestModel> requestModel);

    @POST("booking.php")
    Call<BaseResponse> bookings(@Body RequestModel<BookRideRequestModel> assignRouteRequestRequestModel);

    @POST("card.php")
    Call<CardResponse> cards(@Body RequestModel<CardRequestModel> requestModel);
}