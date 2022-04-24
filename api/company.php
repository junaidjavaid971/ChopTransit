<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../config/database.php';
include_once '../services/CompanyService.php';
include_once '../models/response.php';
include_once '../enums/responsecodes.php';
include_once '../enums/constants.php';
include '../vendor/autoload.php';

/* ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL); */

$database = new Database();
$db = $database->getConnection();

$companyService = new CompanyService($db);

$data = $_POST['Data'];

if ($data == null) {
    $tempRes = json_decode(file_get_contents('php://input'), true);
    $data = $tempRes['Data'];
}

$lvl = $data['lvl'];

if (strcmp($lvl, "1") == 0) {
    $companyName = $data['companyName'];
    $contactNumber = $data['contactNumber'];
    $email = $data['email'];

    echo json_encode($companyService->addCompany($companyName, $contactNumber, $email));
} else if (strcmp($lvl, "2") == 0) {
    echo json_encode($companyService->getAllCompanies());
} else if (strcmp($lvl, "3") == 0) {
    $id = $data['id'];
    echo json_encode($companyService->getCompanyOnID($id));
} else if (strcmp($lvl, "4") == 0) {
    $id = $data['id'];
    $companyName = $data['companyName'];
    $contactNumber = $data['contactNumber'];
    $email = $data['email'];

    echo json_encode($companyService->updateCompany($id, $companyName, $contactNumber, $email));
} else if (strcmp($lvl, "5") == 0) {
    $id = $data['id'];

    echo json_encode($companyService->deleteCompany($id));
}
