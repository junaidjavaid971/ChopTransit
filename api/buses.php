<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../config/database.php';
include_once '../services/BusService.php';
include_once '../models/response.php';
include_once '../enums/responsecodes.php';
include_once '../enums/constants.php';
include '../vendor/autoload.php';

$database = new Database();
$db = $database->getConnection();

$bus = new BusService($db);

$data = $_POST['Data'];

if ($data == null) {
    $tempRes = json_decode(file_get_contents('php://input'), true);
    $data = $tempRes['Data'];
}

$lvl = $data['lvl'];

if (strcmp($lvl, "1") == 0) {
    $busName = $data['busName'];
    $registrationNumber = $data['registrationNumber'];
    $busType = $data['busType'];
    $color = $data['color'];
    $vacancy = $data['vacancy'];
    $assignedTo = 0;

    $response = $bus->addBus($busName, $registrationNumber, $busType, $color, $assignedTo, $vacancy);
    echo json_encode($response);
} else if (strcmp($lvl, "2") == 0) {
    echo json_encode($bus->getAllBuses());
} else if (strcmp($lvl, "3") == 0) {
    $id = $data['id'];
    echo json_encode($bus->getAllBusesByID($id));
} else if (strcmp($lvl, "4") == 0) {
    $assignedDriver = $data['assignedDriver'];
    echo json_encode($bus->getAllBusesByAssignedDriver($assignedDriver));
} else if (strcmp($lvl, "5") == 0) {
    $assignedRoute = $data['assignedRoute'];
    echo json_encode($bus->getAllBusesByAssignedRoute($assignedRoute));
} else if (strcmp($lvl, "6") == 0) {
    $busID = $data['id'];
    $busName = $data['busName'];
    $registrationNumber = $data['registrationNumber'];
    $busType = $data['busType'];
    $color = $data['color'];
    $vacancy = $data['vacancy'];
    $assignedTo = 0;

    echo json_encode($bus->updateBus($busName, $registrationNumber, $busType, $color, $assignedTo, $busID, $vacancy));
} else if (strcmp($lvl, "7") == 0) {
    $id = $data['id'];
    echo json_encode($bus->deleteBus($id));
} else if (strcmp($lvl, "8") == 0) {
    echo json_encode($bus->getAssignedBuses());
}
