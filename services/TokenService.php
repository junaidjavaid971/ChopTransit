<?php

include_once '../enums/constants.php';
include_once '../models/response.php';

class TokenService
{

    // Connection
    public $conn;

    public function __construct($db)
    {
        $this->conn = $db;
    }

    public function saveToken($userID, $token)
    {
        if (strcmp($this->getToken($userID), "") == 0) {
            $saveTokenQuery = "INSERT INTO " . Constants::TOKENS_TABLE . " SET
                            user_id = :user_id,
                            token = :token";

            $stmt = $this->conn->prepare($saveTokenQuery);

            $stmt->bindParam(":user_id", $userID);
            $stmt->bindParam(":token", $token);

            try {
                if ($stmt->execute()) {
                    $response = new Response();
                    $response->code = ResponseCodes::SUCCESS;
                    $response->desc = "Token saved!";
                    return $response;
                } else {
                    $response = new Response();
                    $response->code = ResponseCodes::FAILURE;
                    $response->desc = "An unexpected error occured while saving the token!";
                    return $response;
                }
            } catch (Exception $e) {
                $response = new Response();
                $response->code = ResponseCodes::FAILURE;
                $response->desc = $e->getMessage();
                return $response;
            }
        } else {
            $saveTokenQuery = "UPDATE " . Constants::TOKENS_TABLE . " SET
                                token = :token
                                WHERE user_id = :u_id";

            $stmt = $this->conn->prepare($saveTokenQuery);

            $stmt->bindParam(":u_id", $userID);
            $stmt->bindParam(":token", $token);

            if ($stmt->execute()) {
                $response = new Response();
                $response->code = ResponseCodes::SUCCESS;
                $response->desc = "Token Updated!";
                return $response;
            } else {
                $response = new Response();
                $response->code = ResponseCodes::FAILURE;
                $response->desc = "An unexpected error occured while saving the token!";
                return $response;
            }
        }
    }

    public function getToken($userID)
    {
        $getTokenQuery = "SELECT * FROM " . Constants::TOKENS_TABLE . " WHERE user_id = :user_id LIMIT 0,1";

        $stmt = $this->conn->prepare($getTokenQuery);
        $stmt->bindParam("user_id", $userID);

        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        return $row['token'];
    }

    function sendGCM($message, $id)
    {
        $url = 'https://fcm.googleapis.com/fcm/send';

        $server_key = 'AAAAOHWW6bc:APA91bFk_uLGXS9UObHzAfgjdFeloCC7IK-q2jtw0UeCuo2Gju8L3Z5Zf7hiSQN_NsW7hHK--HNSzCuRgr7R8lkz23h7cxhgeXc8JeF9bCfpdLrZRa6xJmTzJtdVOStEUFJdd72Dpq2-';

        $headers = array(
            'Content-Type:application/json',
            'Authorization:key=' . $server_key
        );

        $fields = array(
            'registration_ids' => array($id),

            'data' => array(
                "title" => $message,
                "message" => $message
            )
        );

        $fields = json_encode($fields);

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $fields);
        $result = curl_exec($ch);
        if ($result === FALSE) {
            die('Oops! FCM Send Error: ' . curl_error($ch));
        }

        echo $result;
        curl_close($ch);
    }

    function send_gcm_notify($id, $title, $message, $isPassenger)
    {
        $userID = "";
        if ($isPassenger) {
            $userID = $this->getPassengerUserID($id);
        } else {
            $userID = $this->getDriverUserID($id);
        }

        $token = $this->getToken($userID);

        define("GOOGLE_API_KEY", "AAAAOHWW6bc:APA91bFk_uLGXS9UObHzAfgjdFeloCC7IK-q2jtw0UeCuo2Gju8L3Z5Zf7hiSQN_NsW7hHK--HNSzCuRgr7R8lkz23h7cxhgeXc8JeF9bCfpdLrZRa6xJmTzJtdVOStEUFJdd72Dpq2-");
        define("GOOGLE_GCM_URL", "https://fcm.googleapis.com/fcm/send");
        $fields = array(
            'to'             => $token,
            'priority'       => "high",
            'data'           => array(
                "title"      => $title,
                "message"    => $message,
                'vibrate'    => 1,
                'sound'      => 1
            )
        );
        $headers = array(
            GOOGLE_GCM_URL,
            'Content-Type: application/json',
            'Authorization: key=' . GOOGLE_API_KEY
        );
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, GOOGLE_GCM_URL);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
        $result = curl_exec($ch);
        if ($result === FALSE) {
            die('Problem occurred: ' . curl_error($ch));
        }

        curl_close($ch);
    }

    public function getPassengerUserID($id)
    {
        $userIDQuery = "SELECT * FROM "  . Constants::PESSENGER_TABLE . " 
                        WHERE id = :id";

        $stmt = $this->conn->prepare($userIDQuery);

        $stmt->bindParam(":id", $id);

        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        return $row['user_id'];
    }

    public function getDriverUserID($id)
    {
        $userIDQuery = "SELECT * FROM "  . Constants::DRIVERS_TABLE . " 
                        WHERE id = :id";

        $stmt = $this->conn->prepare($userIDQuery);

        $stmt->bindParam(":id", $id);

        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        return $row['user_id'];
    }
}
