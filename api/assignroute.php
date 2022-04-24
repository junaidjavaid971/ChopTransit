<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../config/database.php';
include_once '../services/AssignRouteService.php';
include_once '../models/response.php';
include_once '../enums/responsecodes.php';
include_once '../models/Response/Stops.php';
include_once '../enums/constants.php';
include_once '../models/Response/Routes.php';
include_once '../models/Response/Stops.php';
include_once '../models/Response/Bus.php';

/* ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL); */

$database = new Database();
$db = $database->getConnection();

$service = new AssignRouteService($db);

$data = $_POST['Data'];

if ($data == null) {
    $tempRes = json_decode(file_get_contents('php://input'), true);
    $data = $tempRes['Data'];
}

$lvl = $data['lvl'];

if (strcmp($lvl, "1") == 0) {
    $route = new Route();
    $stop = new Stop();
    $bus = new Bus();

    $route = $data['route'];
    $bus = $data['bus'];
    $departureTime = $data['departureTime'];


    echo json_encode($service->assignRoute($departureTime, $route, $bus));
} else if (strcmp($lvl, "2") == 0) {
    $busID = $data['busID'];
    $driverID = $data['driverID'];

    echo json_encode($service->assignDriverWithBus($busID, $driverID));
} else if (strcmp($lvl, "3") == 0) {
    echo json_encode($service->getAssignedRoutes());
} else if (strcmp($lvl, "4") == 0) {
    $id = $data['id'];
    echo json_encode($service->getAssignedRoutesByID($id));
} else if (strcmp($lvl, "5") == 0) {
    $id = $data['driverID'];
    echo json_encode($service->getAssignedRoutesByDriverID($id));
}
