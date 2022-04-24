<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../config/database.php';
include_once '../services/UserAuthService.php';
include_once '../models/response.php';
include_once '../enums/responsecodes.php';
include_once '../enums/constants.php';
include '../vendor/autoload.php';

/* ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL); */

$database = new Database();
$db = $database->getConnection();

$user = new UserAuthService($db);

$data = $_POST['Data'];

if ($data == null) {
    $tempRes = json_decode(file_get_contents('php://input'), true);
    $data = $tempRes['Data'];
}

$lvl = $data['lvl'];

if (strcmp($lvl, "1") == 0) {
    $mobileNumber = $data['phoneNumber'];
    if ($user->deletePreviousCodes($mobileNumber)) {
        $otp = mt_rand(1000, 9999);
        try {
            $basic  = new \Vonage\Client\Credentials\Basic(Constants::API_KEY, Constants::API_SECRET);
            $client = new \Vonage\Client($basic);

            $otp = mt_rand(1000, 9999);

            $response = $client->sms()->send(
                new \Vonage\SMS\Message\SMS($mobileNumber, "Chop Chop", $otp . ' is your verification code from Chop Chop')
            );

            $message = $response->current();
            if ($message->getStatus() == 0) {
                $response = $user->saveVerificationCode($otp, $mobileNumber);
            } else {
                $response = new Response();
                $response->code = ResponseCodes::FAILURE;
                $response->desc = "Sorry, verification code could not sent to your mobile number. Please try again.";
            }
            echo json_encode($response);
        } catch (Exception $e) {
            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = $e->getMessage();

            echo json_encode($response);
        }
    } else {
        $response = new Response();
        $response->code = ResponseCodes::FAILURE;
        $response->desc = "Sorry, verification code could not sent to your mobile number. Please try again.";

        echo json_encode($response);
    }
} else if (strcmp($lvl, "2") == 0) {
    $mobileNumber = $data['phoneNumber'];
    $otp = $data['otp'];

    echo json_encode($user->verifyOTPCode($otp, $mobileNumber));
} else if (strcmp($lvl, "3") == 0) {

    $name = $data['name'];
    $email = $data['email'];
    $phoneNumber = $data['phoneNumber'];

    echo json_encode($user->pessengerSignup($name, $email, $phoneNumber));
} else if (strcmp($lvl, "4") == 0) {

    $phoneNumber = $data['phoneNumber'];

    $pessenger = $user->getPessengerInfo($phoneNumber);
    if ($pessenger != null) {
        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Pessenger Information";
        $response->data = $pessenger;
    } else {
        $response = new Response();
        $response->code = ResponseCodes::FAILURE;
        $response->desc = "Couldn't find the pessenger information!";
    }
    echo json_encode($response);
} else if (strcmp($lvl, "5") == 0) {
    $mobileNumber = $data['phoneNumber'];
    $otp = $data['otp'];

    echo json_encode($user->verifyDriverOTPCode($otp, $mobileNumber));
} else if (strcmp($lvl, "6") == 0) {
    $id = $data['id'];

    $passenger = $user->getPessengerInfoOnID($id);

    if ($passenger != null) {
        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Pessenger Information";
        $response->data = $passenger;
    } else {
        $response = new Response();
        $response->code = ResponseCodes::FAILURE;
        $response->desc = "Couldn't find the pessenger information!";
    }
    echo json_encode($response);
}
