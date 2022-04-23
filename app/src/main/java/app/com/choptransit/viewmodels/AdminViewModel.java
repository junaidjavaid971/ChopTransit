package app.com.choptransit.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import app.com.choptransit.R;
import app.com.choptransit.app.com.choptransit.models.request.StopRequestModel;
import app.com.choptransit.models.observer.BusModel;
import app.com.choptransit.models.observer.CompanyModel;
import app.com.choptransit.models.observer.RouteModel;
import app.com.choptransit.models.observer.StopModel;
import app.com.choptransit.models.request.AdminRequestModel;
import app.com.choptransit.models.request.AssignRouteRequest;
import app.com.choptransit.models.request.BookRideRequestModel;
import app.com.choptransit.models.request.BusReqModel;
import app.com.choptransit.models.request.CompanyReqModel;
import app.com.choptransit.models.request.DriverRequestModel;
import app.com.choptransit.models.request.RequestModel;
import app.com.choptransit.models.request.RouteRequestModel;
import app.com.choptransit.models.response.AssignRouteData;
import app.com.choptransit.models.response.AssignRouteResponse;
import app.com.choptransit.models.response.BaseResponse;
import app.com.choptransit.models.response.BookingResponse;
import app.com.choptransit.models.response.BusResponse;
import app.com.choptransit.models.response.CompanyResponse;
import app.com.choptransit.models.response.DriversResponse;
import app.com.choptransit.models.response.RouteData;
import app.com.choptransit.models.response.RouteResponse;
import app.com.choptransit.models.response.StopsResponse;
import app.com.choptransit.repositories.AdminRepository;
import app.com.choptransit.repositories.AssignRouteRepository;
import app.com.choptransit.repositories.BookingRepository;
import app.com.choptransit.repositories.BusRepository;
import app.com.choptransit.repositories.CompanyRepository;
import app.com.choptransit.repositories.LoginRepository;
import app.com.choptransit.repositories.RoutesRepository;
import app.com.choptransit.repositories.StopsRepository;

public class AdminViewModel extends AndroidViewModel {

    // Observers
    public RouteModel routeModel = new RouteModel();
    public BusModel busModel = new BusModel();
    public StopModel stopModel = new StopModel();
    public CompanyModel companyModel = new CompanyModel();

    // Repositories
    StopsRepository stopsRepository = new StopsRepository();
    BusRepository busRepository = new BusRepository();
    RoutesRepository routesRepository = new RoutesRepository();
    CompanyRepository companyRepository = new CompanyRepository();
    LoginRepository loginRepository = new LoginRepository();
    AssignRouteRepository assignRouteRepository = new AssignRouteRepository();
    BookingRepository bookingRepository = new BookingRepository();
    AdminRepository adminRepository = new AdminRepository();

    // Live Data
    public MutableLiveData<Boolean> dialogLiveData = new MutableLiveData<>();
    public MutableLiveData<AssignRouteResponse> assignRouteReponseLiveData = new MutableLiveData<>();
    public MutableLiveData<BookingResponse> bookingResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<StopsResponse> stopsResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<RouteResponse> routeResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<BusResponse> busResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<CompanyResponse> companyResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<DriversResponse> driversResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<BaseResponse> baseResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public AdminViewModel(@NonNull Application application) {
        super(application);
    }


    public void getStops() {
        StopRequestModel requestModel = new StopRequestModel(2, "", "", 0.0, 0.0);
        RequestModel requestModel1 = new RequestModel();
        requestModel1.setData(requestModel);
        dialogLiveData.postValue(true);

        stopsRepository.stops(requestModel1, body -> {
            dialogLiveData.postValue(false);
            stopsResponseLiveData.postValue((StopsResponse) body);
        });
    }

    public void validateRouteInformation() {
        if (routeModel.getRouteName().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterRouteNameValidation));
        } else if (routeModel.getFare().toString().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterFareValidation));
        } else if (routeModel.getStopsList() == null || routeModel.getStopsList().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_selectStopsValidation));
        } else {
            addRoute();
        }
    }

    public void validateBusInformation() {
        if (busModel.getBusName().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterBusNameValidation));
        } else if (busModel.getBusRegistrationNumber().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterBusRegistrationNumberValidation));
        } else if (busModel.getBusType().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterBusType));
        } else if (busModel.getBusColor().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterBusColor));
        } else if (busModel.getVacancy().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterVacancyValidation));
        } else {
            addBus();
        }
    }

    public void addRoute() {
        if (routeModel.updateRoute) {
            updateRoute();
        } else {
            RouteRequestModel routeRequestModel = new RouteRequestModel();
            routeRequestModel.setRouteName(routeModel.getRouteName());
            routeRequestModel.setFare(Integer.valueOf(routeModel.getFare()));
            routeRequestModel.setStops(routeModel.getStopsList());
            routeRequestModel.setCompanyId(1);
            routeRequestModel.setLvl(3);


            RequestModel requestModel = new RequestModel();
            requestModel.setData(routeRequestModel);
            dialogLiveData.postValue(true);

            routesRepository.addNewRoute(requestModel, body -> {
                dialogLiveData.postValue(false);
                baseResponseLiveData.postValue((BaseResponse) body);
            });
        }
    }

    public void validateStopInformation() {
        if (stopModel.getStopName().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterStopNameValidation));
        } else if (stopModel.getLatitude() == 0.0 || stopModel.getLongitude() == 0.0) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterLocation));
        } else {
            addStop();
        }
    }

    public void validateCompanyInformation() {
        if (companyModel.getCompanyName().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterCompanyNameValidation));
        } else if (companyModel.getCompanyContactNumber().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterContactNumberValidation));
        } else if (companyModel.getCompanyEmail().isEmpty()) {
            errorLiveData.postValue(getApplication().getString(R.string.str_enterContactEmailValidation));
        } else {
            addCompany();
        }
    }

    private void addCompany() {
        if (companyModel.updateCompany) {
            updateCompany();
        } else {
            CompanyReqModel companyReqModel = new CompanyReqModel();
            companyReqModel.setLvl(1);
            companyReqModel.setCompanyName(companyModel.getCompanyName());
            companyReqModel.setContactNumber(companyModel.getCompanyContactNumber());
            companyReqModel.setEmail(companyModel.getCompanyEmail());

            RequestModel requestModel = new RequestModel();
            requestModel.setData(companyReqModel);
            dialogLiveData.postValue(true);

            companyRepository.companies(requestModel, body -> {
                dialogLiveData.postValue(false);
                baseResponseLiveData.postValue((BaseResponse) body);
            });
        }
    }

    public void addStop() {
        if (stopModel.updateStop) {
            updateStop();
        } else {
            StopRequestModel stopRequestModel = new StopRequestModel(1, "", stopModel.getStopName(), stopModel.getLongitude(), stopModel.getLatitude());

            RequestModel requestModel = new RequestModel();
            requestModel.setData(stopRequestModel);
            dialogLiveData.postValue(true);

            stopsRepository.stops(requestModel, body -> {
                dialogLiveData.postValue(false);
                baseResponseLiveData.postValue((BaseResponse) body);
            });
        }
    }

    private void addBus() {
        if (busModel.updateBus) {
            updateBus();
        } else {
            BusReqModel busRequestModel = new BusReqModel();
            busRequestModel.setLvl(1);
            busRequestModel.setBusName(busModel.getBusName());
            busRequestModel.setRegistrationNumber(busModel.getBusRegistrationNumber());
            busRequestModel.setBusType(busModel.getBusType());
            busRequestModel.setColor(busModel.getBusColor());
            busRequestModel.setVacancy(busModel.getVacancy());

            RequestModel requestModel = new RequestModel();
            requestModel.setData(busRequestModel);
            dialogLiveData.postValue(true);

            busRepository.buses(requestModel, body -> {
                dialogLiveData.postValue(false);
                baseResponseLiveData.postValue((BaseResponse) body);
            });
        }
    }

    private void updateBus() {
        BusReqModel busRequestModel = new BusReqModel();
        busRequestModel.setLvl(6);
        busRequestModel.setId(busModel.getId());
        busRequestModel.setBusName(busModel.getBusName());
        busRequestModel.setRegistrationNumber(busModel.getBusRegistrationNumber());
        busRequestModel.setBusType(busModel.getBusType());
        busRequestModel.setColor(busModel.getBusColor());
        busRequestModel.setVacancy(busModel.getVacancy());

        RequestModel requestModel = new RequestModel();
        requestModel.setData(busRequestModel);
        dialogLiveData.postValue(true);

        busRepository.buses(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((BaseResponse) body);
        });
    }

    public void getRoutes() {
        RouteRequestModel routeRequestModel = new RouteRequestModel();
        routeRequestModel.setLvl(4);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(routeRequestModel);
        dialogLiveData.postValue(true);

        routesRepository.getAllRoutes(requestModel, body -> {
            dialogLiveData.postValue(false);
            routeResponseLiveData.postValue((RouteResponse) body);
        });
    }

    public void getRouteByDriverID(String id) {
        AssignRouteRequest routeRequestModel = new AssignRouteRequest();
        routeRequestModel.setLvl(5);
        routeRequestModel.setDriverID(id);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(routeRequestModel);
        dialogLiveData.postValue(true);

        assignRouteRepository.assignRoutes(requestModel, body -> {
            dialogLiveData.postValue(false);
            assignRouteReponseLiveData.postValue((AssignRouteResponse) body);
        });
    }

    public void getSpecificRoutes(String id) {
        RouteRequestModel routeRequestModel = new RouteRequestModel();
        routeRequestModel.setLvl(5);
        routeRequestModel.setId(id);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(routeRequestModel);
        dialogLiveData.postValue(true);

        routesRepository.getAllRoutes(requestModel, body -> {
            dialogLiveData.postValue(false);
            routeResponseLiveData.postValue((RouteResponse) body);
        });
    }

    public void getBuses() {
        BusReqModel busReqModel = new BusReqModel();
        busReqModel.setLvl(2);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(busReqModel);
        dialogLiveData.postValue(true);

        busRepository.buses(requestModel, body -> {
            dialogLiveData.postValue(false);
            busResponseLiveData.postValue((BusResponse) body);
        });
    }

    public void getAssignedBuses() {
        BusReqModel busReqModel = new BusReqModel();
        busReqModel.setLvl(2);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(busReqModel);
        dialogLiveData.postValue(true);

        busRepository.buses(requestModel, body -> {
            dialogLiveData.postValue(false);
            busResponseLiveData.postValue((BusResponse) body);
        });
    }

    public void getCompanies() {
        CompanyReqModel companyReqModel = new CompanyReqModel();
        companyReqModel.setLvl(2);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(companyReqModel);
        dialogLiveData.postValue(true);

        companyRepository.companies(requestModel, body -> {
            dialogLiveData.postValue(false);
            companyResponseLiveData.postValue((CompanyResponse) body);
        });
    }

    public void getCompanyByID(String id) {
        CompanyReqModel companyReqModel = new CompanyReqModel();
        companyReqModel.setLvl(3);
        companyReqModel.setId(id);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(companyReqModel);
        dialogLiveData.postValue(true);

        companyRepository.companies(requestModel, body -> {
            dialogLiveData.postValue(false);
            companyResponseLiveData.postValue((CompanyResponse) body);
        });
    }

    public void getDriverRequests() {
        DriverRequestModel driverRequestModel = new DriverRequestModel(3);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);
        dialogLiveData.postValue(true);

        loginRepository.drivers(requestModel, body -> {
            dialogLiveData.postValue(false);
            driversResponseLiveData.postValue((DriversResponse) body);
        });
    }

    public void getDrivers() {
        DriverRequestModel driverRequestModel = new DriverRequestModel(6);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(driverRequestModel);
        dialogLiveData.postValue(true);

        loginRepository.drivers(requestModel, body -> {
            dialogLiveData.postValue(false);
            driversResponseLiveData.postValue((DriversResponse) body);
        });
    }

    public void deleteStop(@NotNull String id) {
        StopRequestModel stopRequestModel = new StopRequestModel(7, id, "", 0.0, 0.0);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(stopRequestModel);
        dialogLiveData.postValue(true);

        stopsRepository.stops(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((BaseResponse) body);
        });
    }

    public void updateStop() {
        StopRequestModel stopRequestModel = new StopRequestModel(8, stopModel.getId(), stopModel.getStopName(), stopModel.getLatitude(), stopModel.getLongitude());

        RequestModel requestModel = new RequestModel();
        requestModel.setData(stopRequestModel);
        dialogLiveData.postValue(true);

        stopsRepository.stops(requestModel, body -> {
            dialogLiveData.postValue(false);
            stopsResponseLiveData.postValue((StopsResponse) body);
        });
    }

    public void deleteRoute(@NotNull String id) {
        RouteRequestModel routeRequestModel = new RouteRequestModel();
        routeRequestModel.setLvl(9);
        routeRequestModel.setId(id);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(routeRequestModel);
        dialogLiveData.postValue(true);

        routesRepository.getAllRoutes(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((BaseResponse) body);
        });
    }

    public void updateRoute() {
        RouteRequestModel routeRequestModel = new RouteRequestModel();
        routeRequestModel.setRouteName(routeModel.getRouteName());
        routeRequestModel.setFare(Integer.valueOf(routeModel.getFare()));
        routeRequestModel.setStops(routeModel.getStopsList());
        routeRequestModel.setCompanyId(0);
        routeRequestModel.setId(routeModel.getId());
        routeRequestModel.setLvl(10);


        RequestModel requestModel = new RequestModel();
        requestModel.setData(routeRequestModel);
        dialogLiveData.postValue(true);

        routesRepository.addNewRoute(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((BaseResponse) body);
        });
    }

    public void deleteBus(@NotNull String id) {
        BusReqModel busReqModel = new BusReqModel();
        busReqModel.setLvl(7);
        busReqModel.setId(id);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(busReqModel);
        dialogLiveData.postValue(true);

        busRepository.buses(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((BaseResponse) body);
        });
    }

    public void getBusByID(String id) {
        BusReqModel busReqModel = new BusReqModel();
        busReqModel.setLvl(3);
        busReqModel.setId(id);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(busReqModel);
        dialogLiveData.postValue(true);

        busRepository.buses(requestModel, body -> {
            dialogLiveData.postValue(false);
            busResponseLiveData.postValue((BusResponse) body);
        });
    }

    private void updateCompany() {
        CompanyReqModel companyReqModel = new CompanyReqModel();
        companyReqModel.setLvl(4);
        companyReqModel.setId(companyModel.getId());
        companyReqModel.setCompanyName(companyModel.getCompanyName());
        companyReqModel.setContactNumber(companyModel.getCompanyContactNumber());
        companyReqModel.setEmail(companyModel.getCompanyEmail());

        RequestModel requestModel = new RequestModel();
        requestModel.setData(companyReqModel);
        dialogLiveData.postValue(true);

        companyRepository.companies(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((BaseResponse) body);
        });
    }

    public void deleteCompany(@NotNull String id) {
        CompanyReqModel companyReqModel = new CompanyReqModel();
        companyReqModel.setLvl(5);
        companyReqModel.setId(id);

        RequestModel requestModel = new RequestModel();
        requestModel.setData(companyReqModel);
        dialogLiveData.postValue(true);

        companyRepository.companies(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((BaseResponse) body);
        });
    }

    public void assignRoute(@NotNull RouteData selectedRoute, BusResponse.@NotNull BusData selectedBus, @NotNull String departureTime) {
        AssignRouteRequest routeRequest = new AssignRouteRequest();
        routeRequest.setRoute(selectedRoute);
        routeRequest.setBus(selectedBus);
        routeRequest.setDepartureTime(departureTime);
        routeRequest.setLvl(1);

        RequestModel<AssignRouteRequest> requestModel = new RequestModel<>();
        requestModel.setData(routeRequest);

        dialogLiveData.postValue(true);
        assignRouteRepository.assignRoutes(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((BaseResponse) body);
        });
    }

    public void assignBusToDriver(@NotNull String busID, String driverID) {
        AssignRouteRequest routeRequest = new AssignRouteRequest();
        routeRequest.setBusID(busID);
        routeRequest.setDriverID(driverID);
        routeRequest.setLvl(2);

        RequestModel<AssignRouteRequest> requestModel = new RequestModel<>();
        requestModel.setData(routeRequest);

        dialogLiveData.postValue(true);
        assignRouteRepository.assignRoutes(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((BaseResponse) body);
        });
    }

    public void getAssignedRoutes() {
        AssignRouteRequest requestModel = new AssignRouteRequest();
        requestModel.setLvl(3);

        RequestModel requestModel1 = new RequestModel();
        requestModel1.setData(requestModel);
        dialogLiveData.postValue(true);

        assignRouteRepository.assignRoutes(requestModel1, body -> {
            dialogLiveData.postValue(false);
            assignRouteReponseLiveData.postValue((AssignRouteResponse) body);
        });
    }

    public void bookRide(String passengerID, String assignedRouteID, String driverID, String stopName) {
        BookRideRequestModel bookRideRequestModel = new BookRideRequestModel();
        bookRideRequestModel.setLvl(1);
        bookRideRequestModel.setAssignedRouteID(assignedRouteID);
        bookRideRequestModel.setPassengerID(passengerID);
        bookRideRequestModel.setStopName(stopName);
        bookRideRequestModel.setDriverID(driverID);

        RequestModel<BookRideRequestModel> requestModel = new RequestModel<>();
        requestModel.setData(bookRideRequestModel);

        dialogLiveData.postValue(true);

        bookingRepository.booking(requestModel, body -> {
            dialogLiveData.postValue(false);
            baseResponseLiveData.postValue((BaseResponse) body);
        });
    }

    public void getCounts() {
        AdminRequestModel adminRequestModel = new AdminRequestModel();
        adminRequestModel.setLvl(1);

        RequestModel<AdminRequestModel> requestModel = new RequestModel<>();
        requestModel.setData(adminRequestModel);

        adminRepository.getCounts(requestModel, body -> {
            baseResponseLiveData.postValue((BaseResponse) body);
        });
    }
}
