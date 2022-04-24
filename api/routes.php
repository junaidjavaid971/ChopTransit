<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../config/database.php';
include_once '../services/RouteService.php';
include_once '../models/response.php';
include_once '../enums/responsecodes.php';
include_once '../models/Response/Stops.php';
include_once '../enums/constants.php';

/* ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL); */

$database = new Database();
$db = $database->getConnection();

$route = new RouteService($db);

$data = $_POST['Data'];

if ($data == null) {
    $tempRes = json_decode(file_get_contents('php://input'), true);
    $data = $tempRes['Data'];
}

$lvl = $data['lvl'];

if (strcmp($lvl, "1") == 0) {
    $latitude = $data['latitude'];
    $longitude = $data['longitude'];
    $stopName = $data['stopName'];

    $response = $route->addStop($latitude, $longitude, $stopName);
    echo json_encode($response);
} else if (strcmp($lvl, "2") == 0) {
    echo json_encode($route->getAllStops());
} else if (strcmp($lvl, "3") == 0) {
    $routeName = $data['routeName'];
    $fare = $data['fare'];
    $stops = $data['stops'];
    $companyID = $data['companyId'];

    $stopIDs = "";
    foreach ($stops as $stop) {
        $stopID = $stop['id'];

        $stopIDs .= $stopID . ",";
    }

    $stopIDs = substr($stopIDs, 0, -1);

    echo json_encode($route->addRoute($routeName, $fare, $stopIDs, $companyID));
} else if (strcmp($lvl, "4") == 0) {
    echo json_encode($route->getAllRoutes());
} else if (strcmp($lvl, "5") == 0) {
    $routeID = $data['id'];
    echo json_encode($route->getRouteOnID($routeID));
} else if (strcmp($lvl, "6") == 0) {
    $stopID = $data['id'];
    $response = new Response();
    $response->code = ResponseCodes::SUCCESS;
    $response->desc = "Stop Information";
    $response->data = $route->getStopOnID($stopID);
    echo json_encode($response);
} else if (strcmp($lvl, "7") == 0) {
    $stopID = $data['id'];
    echo json_encode($route->deleteStop($stopID));
} else if (strcmp($lvl, "8") == 0) {
    $stopID = $data['id'];
    $latitude = $data['latitude'];
    $longitude = $data['longitude'];
    $stopName = $data['stop_name'];
    echo json_encode($route->updateStop($stopID, $latitude, $longitude, $stopName));
} else if (strcmp($lvl, "9") == 0) {
    $routeID = $data['id'];
    echo json_encode($route->deleteRoute($routeID));
} else if (strcmp($lvl, "10") == 0) {
    $routeName = $data['routeName'];
    $fare = $data['fare'];
    $stops = $data['stops'];
    $routeID = $data['id'];
    $companyID = $data['companyId'];

    $stopIDs = "";
    foreach ($stops as $stop) {
        $stopID = $stop['id'];

        $stopIDs .= $stopID . ",";
    }

    $stopIDs = substr($stopIDs, 0, -1);

    echo json_encode($route->updateRoute($routeName, $fare, $stopIDs, $companyID, $routeID));
}
