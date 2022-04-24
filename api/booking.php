<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../config/database.php';
include_once '../services/BookingService.php';
include_once '../models/response.php';
include_once '../enums/responsecodes.php';
include_once '../models/Response/Stops.php';
include_once '../enums/constants.php';
include_once '../models/Response/Routes.php';
include_once '../models/Response/Stops.php';
include_once '../models/Response/Bus.php';
include "qrcode.php";

ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

$database = new Database();
$db = $database->getConnection();

$bookingService = new BookingService($db);

$data = $_POST['Data'];

if ($data == null) {
    $tempRes = json_decode(file_get_contents('php://input'), true);
    $data = $tempRes['Data'];
}

$lvl = $data['lvl'];

if (strcmp($lvl, "1") == 0) {
    $passengerID = $data['passengerID'];
    $assignedRouteID = $data['assignedRouteID'];
    $stopName = $data['stopName'];
    $driverID = $data['driverID'];

    echo json_encode($bookingService->placeBooking($passengerID, $assignedRouteID, $stopName, $driverID));
} else if (strcmp($lvl, "2") == 0) {
    $bookingID = $data['id'];

    $booking = $bookingService->getBookingByID($bookingID);


    $response = new Response();
    $response->code = ResponseCodes::SUCCESS;
    $response->desc = "Booking";
    $response->data = $booking;

    echo json_encode($response);
} else if (strcmp($lvl, "3") == 0) {
    echo json_encode($bookingService->getAllBookings());
} else if (strcmp($lvl, "5") == 0) {
    $id = $data['id'];
    $bookingStatus = $data['status'];

    echo json_encode($bookingService->updateBookingStatus($id, $bookingStatus));
}
