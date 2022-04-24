<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../config/database.php';
include_once '../services/DriverService.php';
include_once '../services/BookingService.php';
include_once '../models/response.php';
include_once '../enums/responsecodes.php';
include_once '../enums/constants.php';
include '../vendor/autoload.php';

/* ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL); */

$database = new Database();
$db = $database->getConnection();

$driver = new DriverService($db);

$data = $_POST['Data'];

if ($data == null) {
    $tempRes = json_decode(file_get_contents('php://input'), true);
    $data = $tempRes['Data'];
}

$lvl = $data['lvl'];

if (strcmp($lvl, "1") == 0) {

    $phoneNumber = $data['phoneNumber'];
    $firstName = $data['firstName'];
    $lastName = $data['lastName'];
    $email = $data['email'];
    $companyID = $data['companyID'];
    $response = $driver->registerDriver($phoneNumber, $firstName, $lastName, $email, $companyID);
    echo json_encode($response);
} else if (strcmp($lvl, "2") == 0) {

    $id = $data['id'];
    $driverProfile = $driver->getDriverProfileByID($id);
    $response = new Response();
    if (empty($driverProfile->id)) {
        $response->code = ResponseCodes::FAILURE;
        $response->desc = "Sorry, We couldn't find the driver's profile!";

        echo json_encode($response);
        return;
    }
    $response->code = ResponseCodes::SUCCESS;
    $response->desc = "Driver Profile";
    $response->data = $driverProfile;
    echo json_encode($response);
} else if (strcmp($lvl, "3") == 0) {

    echo json_encode($driver->getAllDriverRequests());
} else if (strcmp($lvl, "4") == 0) {

    $id = $data['id'];
    echo json_encode($driver->approveDriverRequest($id));
} else if (strcmp($lvl, "5") == 0) {

    $id = $data['id'];
    echo json_encode($driver->rejectBookingRequest($id));
} else if (strcmp($lvl, "6") == 0) {

    echo json_encode($driver->getAllApprovedDrivers());
} else if (strcmp($lvl, "7") == 0) {
    $bookingID = $data['id'];
    echo json_encode($driver->approveRideRequest($bookingID));
} else if (strcmp($lvl, "8") == 0) {
    $bookingID = $data['id'];
    echo json_encode($driver->deleteRideRequest($bookingID));
} else if (strcmp($lvl, "9") == 0) {
    $driverID = $data['id'];
    echo json_encode($driver->signIn($driverID));
} else if (strcmp($lvl, "10") == 0) {
    $driverID = $data['id'];
    echo json_encode($driver->signOff($driverID));
} else if (strcmp($lvl, "11") == 0) {
    $driverID = $data['id'];
    echo json_encode($driver->checkSignInStatus($driverID));
} else if (strcmp($lvl, "12") == 0) {
    $driverID = $data['id'];

    $bookingService = new BookingService($db);
    echo json_encode($bookingService->getPendingBookings($driverID));
} else if (strcmp($lvl, "13") == 0) {
    $driverID = $data['id'];

    $bookingService = new BookingService($db);
    echo json_encode($bookingService->getConfirmedBookings($driverID));
} else if (strcmp($lvl, "14") == 0) {
    $driverID = $data['id'];

    $bookingService = new BookingService($db);
    echo json_encode($bookingService->getActiveOrConfirmedBookings($driverID));
} else if (strcmp($lvl, "15") == 0) {
    $driverID = $data['id'];

    $bookingService = new BookingService($db);
    echo json_encode($bookingService->getFinishedBookings($driverID));
} else if (strcmp($lvl, "16") == 0) {
    $bookingID = $data['id'];
    $status = "confirmed";

    $bookingService = new BookingService($db);
    echo json_encode($bookingService->updateBookingStatus($bookingID, $status));
} else if (strcmp($lvl, "17") == 0) {
    $bookingID = $data['id'];
    $status = "active";

    $bookingService = new BookingService($db);
    echo json_encode($bookingService->updateBookingStatus($bookingID, $status));
} else if (strcmp($lvl, "18") == 0) {
    $bookingID = $data['id'];
    $status = "finished";

    $bookingService = new BookingService($db);
    echo json_encode($bookingService->finishRide($bookingID, $status));
} else if (strcmp($lvl, "19") == 0) {
    $bookingID = $data['id'];
    $status = "rejected";

    $bookingService = new BookingService($db);
    echo json_encode($bookingService->updateBookingStatus($bookingID, $status));
} else if (strcmp($lvl, "20") == 0) {
    $bookingID = $data['id'];
    $status = "missed";

    $bookingService = new BookingService($db);
    echo json_encode($bookingService->updateBookingStatus($bookingID, $status));
} else if (strcmp($lvl, "21") == 0) {
    $id = $data['id'];

    $bookingService = new BookingService($db);
    echo json_encode($bookingService->getPassengerPendingBookings($id));
} else if (strcmp($lvl, "22") == 0) {
    $id = $data['id'];

    $bookingService = new BookingService($db);
    echo json_encode($bookingService->getPassengerConfirmedBookings($id));
} else if (strcmp($lvl, "23") == 0) {
    $id = $data['id'];

    $bookingService = new BookingService($db);
    echo json_encode($bookingService->getPassengerActiveBookings($id));
} else if (strcmp($lvl, "24") == 0) {
    $id = $data['id'];

    $bookingService = new BookingService($db);
    echo json_encode($bookingService->getPassengerMissedBookings($id));
} else if (strcmp($lvl, "25") == 0) {
    $id = $data['id'];
    $latitude = $data['latitude'];
    $longitude = $data['longitude'];
    $bookingService = new BookingService($db);
    echo json_encode($bookingService->updateLocation($latitude, $longitude, $id));
} else if (strcmp($lvl, "26") == 0) {
    $id = $data['id'];
    $bookingService = new BookingService($db);
    echo json_encode($bookingService->getDriverLocation($id));
}
 else if (strcmp($lvl, "27") == 0) {
    $id = $data['id'];
    echo json_encode($driver->deleteDriver($id));
} else if (strcmp($lvl, "28") == 0) {
    $id = $data['id'];

    $bookingService = new BookingService($db);
    echo json_encode($bookingService->getPassengerFinishedBookings($id));
}
