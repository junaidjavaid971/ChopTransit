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
    $cardService->cardNumber = $data['cardNumber'];
    $cardService->expiryMonth = $data['expiryMonth'];
    $cardService->expiryYear = $data['expiryYear'];
    $cardService->cvc = $data['cvc'];
    $cardService->email = $data['email'];
    $cardService->cardHolderName = $data['cardHolderName'];
    $cardService->id = $data['id'];

    $response = $cardService->createCustomer();
} else if (strcmp($lvl, "2") == 0) {
    $cardService->customerID = $data['customerID'];
    $amount = $data['amount'];
    $cardService->id = $data['id'];

    $amount = $amount * 100;
    echo json_encode($cardService->performTransaction($amount));
} else if (strcmp($lvl, "3") == 0) {
    $cardService->customerID = $data['customerID'];
    $cardService->id = $data['id'];

    echo json_encode($cardService->deleteCustomer());
} else if (strcmp($lvl, "4") == 0) {
    $cardService->cardID = $data['cardID'];
    $cardService->customerID = $data['customerID'];

    echo json_encode($cardService->getCardDetails());
} else if (strcmp($lvl, "5") == 0) {
    $cardService->getAllCustomers();
} else if (strcmp($lvl, "6") == 0) {
    $cardService->cardNumber = $data['cardNumber'];
    $cardService->expiryMonth = $data['expiryMonth'];
    $cardService->expiryYear = $data['expiryYear'];
    $cardService->cvc = $data['cvc'];
    $cardService->cardHolderName = $data['cardHolderName'];
    $cardService->id = $data['id'];
    $cardService->customerID = $data['customerID'];
    $cardService->cardID = $data['cardID'];
    $cardService->email = $data['email'];

    echo json_encode($cardService->updateCustomerCard());
} else if (strcmp($lvl, "7") == 0) {
    $cardService->cardID = $data['cardID'];
    $cardService->customerID = $data['customerID'];
    $cardService->id = $data['id'];

    echo json_encode($cardService->deleteCustomerCard());
}
