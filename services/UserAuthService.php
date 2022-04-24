<?php
include_once '../models/response.php';
include_once '../models/Response/Pessenger.php';
include_once 'DriverService.php';
include_once '../enums/constants.php';

class UserAuthService
{

    // Connection
    private $conn;

    // Columns
    public $id;
    public $name;
    public $email;
    public $phoneNumber;
    public $otpCode;
    public $expiryTime;

    // Db connection
    public function __construct($db)
    {
        $this->conn = $db;
    }

    public function saveVerificationCode($otpCode, $phoneNumber)
    {
        $otpQuery = "INSERT INTO 
        " . Constants::VERIFICATION_CODES . "
            SET
            otp_code = :otp_code,
            phone_number = :phone_number,
            created_at = :created_at";

        $stmt = $this->conn->prepare($otpQuery);
        // bind data
        $createdAt = date('d-m-y h:i:s');
        $stmt->bindParam(":otp_code", $otpCode);
        $stmt->bindParam(":phone_number", $phoneNumber);
        $stmt->bindParam(":created_at", $createdAt);

        if ($stmt->execute()) {
            $response = new Response();
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "A verification code is sent to " . $phoneNumber;
            $response->data = $otpCode;
            return $response;
        } else {
            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "Sorry, verification code could not sent to your mobile number. Please try again.";
            return $response;
        }
    }

    public function deletePreviousCodes($phoneNumber)
    {
        $deleteQuery = "DELETE FROM 
        " . Constants::VERIFICATION_CODES . "
            WHERE :phone_number = :phone_number";

        $stmt = $this->conn->prepare($deleteQuery);

        // bind data
        $stmt->bindParam(":phone_number", $phoneNumber);

        return $stmt->execute();
    }

    public function verifyOTPCode($otp, $phoneNumber)
    {
        $getOtpQuery = "SELECT * FROM " . Constants::VERIFICATION_CODES . " WHERE otp_code = :otp_code AND phone_number = :phone_number";

        $stmt = $this->conn->prepare($getOtpQuery);
        // bind data
        $stmt->bindParam(":otp_code", $otp);
        $stmt->bindParam(":phone_number", $phoneNumber);

        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($stmt->rowCount() > 0) {
            $this->expiryTime =  $row['created_at'];
            $this->otpCode = $row['otp_code'];
            $this->phoneNumber = $row['phone_number'];
            $now = new DateTime(date('d-m-y h:i:s'));
            $this->expiryTime = new DateTime($this->expiryTime);

            $difference = $this->expiryTime->diff($now);

            if ($difference->i >= 1) {
                $response = new Response();
                $response->code = ResponseCodes::FAILURE;
                $response->desc = "Your verification code is expired. Please try again!";
                return $response;
            } else {
                $this->deletePreviousCodes($phoneNumber);

                $pessenger = $this->getPessengerInfo($phoneNumber);

                if ($pessenger != null) {
                    $response = new Response();
                    $response->code = ResponseCodes::SUCCESS;
                    $response->desc = "Verification code is verified successfully!";
                    $response->data = $pessenger;
                    return $response;
                } else {
                    $response = new Response();
                    $response->code = ResponseCodes::SUCCESS;
                    $response->desc = "Verification code is verified successfully!";
                    return $response;
                }
            }
        } else {
            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "Sorry, your verification code is incorrect. Please try again!";
            return $response;
        }
    }

    public function verifyDriverOTPCode($otp, $phoneNumber)
    {
        $getOtpQuery = "SELECT * FROM " . Constants::VERIFICATION_CODES . " WHERE otp_code = :otp_code AND phone_number = :phone_number";

        $stmt = $this->conn->prepare($getOtpQuery);
        // bind data
        $stmt->bindParam(":otp_code", $otp);
        $stmt->bindParam(":phone_number", $phoneNumber);

        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($stmt->rowCount() > 0) {
            $this->expiryTime =  $row['created_at'];
            $this->otpCode = $row['otp_code'];
            $this->phoneNumber = $row['phone_number'];
            $now = new DateTime(date('d-m-y h:i:s'));
            $this->expiryTime = new DateTime($this->expiryTime);

            $difference = $this->expiryTime->diff($now);

            if ($difference->i >= 1) {
                $response = new Response();
                $response->code = ResponseCodes::FAILURE;
                $response->desc = "Your verification code is expired. Please try again!";
                return $response;
            } else {
                $this->deletePreviousCodes($phoneNumber);

                $driverService = new DriverService($this->conn);
                $driver = $driverService->getDriverProfileByNumber($phoneNumber);

                if ($driver != null) {
                    $response = new Response();
                    $response->code = ResponseCodes::SUCCESS;
                    $response->desc = "Verification code is verified successfully!";
                    $response->data = $driver;
                    return $response;
                } else {
                    $response = new Response();
                    $response->code = ResponseCodes::SUCCESS;
                    $response->desc = "Verification code is verified successfully!";
                    return $response;
                }
            }
        } else {
            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "Sorry, your verification code is incorrect. Please try again!";
            return $response;
        }
    }

    public function pessengerSignup($name, $email, $phoneNumber)
    {
        $passenger = $this->getPessengerInfo($phoneNumber);
        if ($passenger == null) {
            $pessengerSignupQuery = "INSERT INTO 
                                    " . Constants::PESSENGER_TABLE . "
                                        SET
                                        name = :name,
                                        email = :email,
                                        user_id = :user_id,
                                        phone_number = :phone_number";

            $stmt = $this->conn->prepare($pessengerSignupQuery);
            $randomString = $this->generateRandomString();

            // bind data
            $stmt->bindParam(":name", $name);
            $stmt->bindParam(":email", $email);
            $stmt->bindParam(":phone_number", $phoneNumber);
            $stmt->bindParam(":user_id", $randomString);

            if ($stmt->execute()) {
                $response = new Response();
                $response->code = ResponseCodes::SUCCESS;
                $response->desc = "Your account is created successfully!";
                $response->data = $this->getPessengerInfo($phoneNumber);
                return $response;
            } else {
                $response = new Response();
                $response->code = ResponseCodes::FAILURE;
                $response->desc = "Sorry, there is a problem in creating your account. Please try again!";
                return $response;
            }
        } else {
            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An account already exists for this phone number. Please try with a different one!";
            return $response;
        }
    }

    public function getPessengerInfo($phoneNumber)
    {
        $getOtpQuery = "SELECT * FROM " . Constants::PESSENGER_TABLE . " WHERE phone_number = :phone_number";

        $stmt = $this->conn->prepare($getOtpQuery);
        // bind data
        $stmt->bindParam(":phone_number", $phoneNumber);

        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($stmt->rowCount() > 0) {
            $pessenger = new Pessenger();
            $pessenger->id =  $row['id'];
            $pessenger->name =  $row['name'];
            $pessenger->email = $row['email'];
            $pessenger->userID = $row['user_id'];
            $pessenger->phoneNumber = $row['phone_number'];
            $pessenger->customerID = $row['customer_id'];
            $pessenger->cardID = $row['card_id'];

            return $pessenger;
        } else {
            return null;
        }
    }

    public function getPessengerInfoOnID($id)
    {
        $getOtpQuery = "SELECT * FROM " . Constants::PESSENGER_TABLE . " WHERE id = :id";

        $stmt = $this->conn->prepare($getOtpQuery);
        // bind data
        $stmt->bindParam(":id", $id);

        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($stmt->rowCount() > 0) {
            $pessenger = new Pessenger();
            $pessenger->id =  $row['id'];
            $pessenger->name =  $row['name'];
            $pessenger->email = $row['email'];
            $pessenger->userID = $row['user_id'];
            $pessenger->phoneNumber = $row['phone_number'];
            $pessenger->customerID = $row['customer_id'];
            $pessenger->cardID = $row['card_id'];

            return $pessenger;
        } else {
            return null;
        }
    }

    function generateRandomString()
    {
        $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
        $charactersLength = strlen($characters);
        $randomString = '';
        for ($i = 0; $i < 16; $i++) {
            $randomString .= $characters[rand(0, $charactersLength - 1)];
        }
        return $randomString;
    }

    function sendGCM($message, $id)
    {


        $url = 'https://fcm.googleapis.com/fcm/send';

        $fields = array(
            'registration_ids' => array(
                $id
            ),
            'data' => array(
                "message" => $message
            )
        );
        $fields = json_encode($fields);

        $headers = array(
            'Authorization: key=' . "YOUR_KEY_HERE",
            'Content-Type: application/json'
        );

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $fields);

        $result = curl_exec($ch);
        echo $result;
        curl_close($ch);
    }
}
