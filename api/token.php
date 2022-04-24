<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../config/database.php';
include_once '../enums/constants.php';
include_once '../enums/responsecodes.php';
include_once '../services/TokenService.php';
include_once '../models/response.php';

$database = new Database();
$db = $database->getConnection();

$tokenService = new TokenService($db);

$data = $_POST['Data'];

if ($data == null) {
    $tempRes = json_decode(file_get_contents('php://input'), true);
    $data = $tempRes['Data'];
}

$lvl = $data['lvl'];

if (strcmp($lvl, "1") == 0) {
    $userID = $data['userID'];
    $token = $data['token'];

    echo json_encode($tokenService->saveToken($userID, $token));
} else if (strcmp($lvl, "2") == 0) {
    $userID = $data['userID'];
    echo $tokenService->getToken($userID);
}
