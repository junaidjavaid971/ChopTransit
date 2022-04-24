<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../config/database.php';
include_once '../models/response.php';
include_once '../enums/responsecodes.php';
include_once '../enums/constants.php';
include_once '../services/CardService.php';
include '../vendor/autoload.php';

$database = new Database();
$db = $database->getConnection();

$cardService = new CardService($db);

$data = $_POST['Data'];

if ($data == null) {
    $tempRes = json_decode(file_get_contents('php://input'), true);
    $data = $tempRes['Data'];
}

$lvl = $data['lvl'];

if (strcmp($lvl, "1") == 0) {
    $amount = $data['amount'];

    $amount =  $amount * 100;
    echo json_encode($cardService->createAliPayIntent($amount));
}
