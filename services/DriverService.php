<?php

include_once '../models/response.php';
include_once '../models/Response/Driver.php';
include_once '../models/Response/Booking.php';
include_once '../models/Response/Bus.php';
include_once 'BusService.php';
include_once 'TokenService.php';

class DriverService
{

    // Connection
    public $conn;
    public $tokenService;

    public function __construct($db)
    {
        $this->conn = $db;
        $this->tokenService = new TokenService($db);
    }

    public function registerDriver($phoneNumber, $firstName, $lastName, $email, $companyID)
    {
        $registerDriverQuery = "INSERT INTO " . Constants::DRIVERS_TABLE . " SET
        phone_number = :phone_number,
        first_name = :first_name,
        last_name = :last_name,
        email = :email,
        company_id = :company_id,
        user_id = :user_id,
        is_approved = :is_approved";

        $stmt = $this->conn->prepare($registerDriverQuery);
        $isApproved = false;
        $randomString = $this->generateRandomString();

        $stmt->bindParam(":phone_number", $phoneNumber);
        $stmt->bindParam(":first_name", $firstName);
        $stmt->bindParam(":last_name", $lastName);
        $stmt->bindParam(":email", $email);
        $stmt->bindParam(":company_id", $companyID);
        $stmt->bindParam(":user_id", $randomString);
        $stmt->bindParam(":is_approved", $isApproved);

        $response = new Response();
        if ($stmt->execute()) {
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Your request has been sent to admin for further verification!";
            $response->data = $this->getDriverProfileByID($this->conn->lastInsertId());
        } else {
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An error occured while registering the driver. Please try again!";
        }

        return $response;
    }

    public function getDriverProfileByID($id)
    {
        $getDriverQuery = "SELECT driver.*, companies.company_name FROM " . Constants::DRIVERS_TABLE . "
        INNER JOIN " . Constants::COMPANY_TABLE . " on companies.id = driver.company_id WHERE driver.id = :id";

        $stmt = $this->conn->prepare($getDriverQuery);

        $stmt->bindParam(":id", $id);

        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $driver = new Driver();
        $driver->id = $row['id'];
        $driver->firstName = $row['first_name'];
        $driver->lastName = $row['last_name'];
        $driver->email = $row['email'];
        $driver->phoneNumber = $row['phone_number'];
        $driver->companyID = $row['company_id'];
        $driver->companyName = $row['company_name'];
        $driver->userID = $row['user_id'];
        $driver->isApproved = $row['is_approved'];
        $driver->signedIn = $row['signed_in'];

        $bus = new Buses();

        $bus = $this->getAllBusesByAssignedDriver($driver->id);

        $driver->busName = $bus->busName;
        $driver->busRegNo = $bus->busRegNo;

        if (empty($bus->busName)) {
            $driver->busName = "";
            $driver->busRegNo = "";
        }

        return $driver;
    }


    public function getAllBusesByAssignedDriver($assignedDriver)
    {
        $getAllBusesQuery = "SELECT * FROM " . Constants::BUSES_TABLE . " WHERE assigned_to = :assigned_to LIMIT 1";
        $stmt = $this->conn->prepare($getAllBusesQuery);

        $stmt->bindParam(":assigned_to", $assignedDriver);
        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $bus = new Bus();

        $bus->id = $row['id'];
        $bus->busName = $row['bus_name'];
        $bus->busRegNo = $row['bus_registration_number'];
        $bus->busType = $row['bus_type'];
        $bus->busColor = $row['bus_color'];
        $bus->vacancy = $row['vacancy'];
        $bus->assignedTo = $row['assigned_to'];

        return $bus;
    }


    public function getDriverProfileByNumber($contactNumber)
    {
        $getDriverQuery = "SELECT driver.*, companies.company_name FROM " . Constants::DRIVERS_TABLE . "
        INNER JOIN " . Constants::COMPANY_TABLE . " on companies.id = driver.company_id
        WHERE driver.phone_number = :phone_number";

        $stmt = $this->conn->prepare($getDriverQuery);

        $stmt->bindParam(":phone_number", $contactNumber);

        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($stmt->rowCount() > 0) {
            $driver = new Driver();
            $driver->id = $row['id'];
            $driver->firstName = $row['first_name'];
            $driver->lastName = $row['last_name'];
            $driver->email = $row['email'];
            $driver->phoneNumber = $row['phone_number'];
            $driver->companyID = $row['company_id'];
            $driver->companyName = $row['company_name'];
            $driver->userID = $row['user_id'];
            $driver->isApproved = $row['is_approved'];
            $driver->signedIn = $row['signed_in'];

            $bus = new Buses();

            $bus = $this->getAllBusesByAssignedDriver($driver->id);

            $driver->busName = $bus->busName;
            $driver->busRegNo = $bus->busRegNo;

            if (empty($bus->busName)) {
                $driver->busName = "";
                $driver->busRegNo = "";
            }

            return $driver;
        } else {
            return null;
        }
    }

    public function getAllApprovedDrivers()
    {
        $getDriverRequestsQuery = "SELECT driver.*, companies.company_name FROM " . Constants::DRIVERS_TABLE . "
        INNER JOIN " . Constants::COMPANY_TABLE . " on companies.id = driver.company_id
        WHERE driver.is_approved = :is_approved";

        $stmt = $this->conn->prepare($getDriverRequestsQuery);

        $isApproved = true;

        $stmt->bindParam(":is_approved", $isApproved);

        $stmt->execute();

        $driverRequests = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $driver = new Driver();
            $driver->id = $row['id'];
            $driver->firstName = $row['first_name'];
            $driver->lastName = $row['last_name'];
            $driver->email = $row['email'];
            $driver->phoneNumber = $row['phone_number'];
            $driver->companyID = $row['company_id'];
            $driver->companyName = $row['company_name'];
            $driver->userID = $row['user_id'];
            $driver->isApproved = $row['is_approved'];

            $bus = new Buses();

            $bus = $this->getAllBusesByAssignedDriver($driver->id);

            $driver->busName = $bus->busName;
            $driver->busRegNo = $bus->busRegNo;

            if (empty($bus->busName)) {
                $driver->busName = "";
                $driver->busRegNo = "";
            }

            array_push($driverRequests, $driver);
        }

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Driver Requests";
        $response->data = $driverRequests;

        return $response;
    }

    public function getAllDriverRequests()
    {
        $getDriverRequestsQuery = "SELECT driver.*, companies.company_name FROM " . Constants::DRIVERS_TABLE . "
        INNER JOIN " . Constants::COMPANY_TABLE . " on companies.id = driver.company_id
        WHERE driver.is_approved = :is_approved";

        $stmt = $this->conn->prepare($getDriverRequestsQuery);

        $isApproved = false;

        $stmt->bindParam(":is_approved", $isApproved);

        $stmt->execute();

        $driverRequests = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $driver = new Driver();
            $driver->id = $row['id'];
            $driver->firstName = $row['first_name'];
            $driver->lastName = $row['last_name'];
            $driver->email = $row['email'];
            $driver->phoneNumber = $row['phone_number'];
            $driver->companyID = $row['company_id'];
            $driver->companyName = $row['company_name'];
            $driver->isApproved = $row['is_approved'];
            $driver->userID = $row['user_id'];

            array_push($driverRequests, $driver);
        }

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Driver Requests";
        $response->data = $driverRequests;

        return $response;
    }

    public function approveDriverRequest($id)
    {
        $approveDriverQuery = "UPDATE " . Constants::DRIVERS_TABLE . " SET is_approved = :is_approved WHERE id = :id";

        $stmt = $this->conn->prepare($approveDriverQuery);

        $isApproved = true;

        $stmt->bindParam(":is_approved", $isApproved);
        $stmt->bindParam(":id", $id);

        $response = new Response();

        $stmt->execute();

        $this->tokenService->send_gcm_notify($id, "Profile Approved", "Congratulations, your profile is approved by the admin!", false);

        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Driver approved!";

        return $response;
    }

    public function rejectBookingRequest($id)
    {
        $approveDriverQuery = "DELETE from " . Constants::DRIVERS_TABLE . "  WHERE id = :id";

        $stmt = $this->conn->prepare($approveDriverQuery);

        $stmt->bindParam(":id", $id);

        $response = new Response();

        $stmt->execute();

        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Driver request is deleted!";

        return $response;
    }

    public function deleteDriver($id)
    {
        $approveDriverQuery = "DELETE from " . Constants::DRIVERS_TABLE . "  WHERE id = :id";

        $stmt = $this->conn->prepare($approveDriverQuery);

        $stmt->bindParam(":id", $id);

        $response = new Response();

        $stmt->execute();

        $this->updateBusAssigning($id);

        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Driver deleted!";

        return $response;
    }

    public function updateBusAssigning($id)
    {
        $updateBusAssigning = "UPDATE "  . Constants::BUSES_TABLE . " 
                                SET assigned_to = :assigned_to 
                                WHERE assigned_to = :driver_id";

        $assignedID = 0;
        $stmt = $this->conn->prepare($updateBusAssigning);

        $stmt->bindParam(":assigned_to", $assignedID);
        $stmt->bindParam(":driver_id", $id);

        $stmt->execute();
    }

    public function approveRideRequest($bookingID)
    {
        $approveBookingQuery = "UPDATE " . Constants::BOOKINGS_TABLE . " SET
        status = :status WHERE id = :id";

        $stmt = $this->conn->prepare($approveBookingQuery);

        $bookingStatus = "confirmed";

        $stmt->bindParam(":status", $bookingStatus);
        $stmt->bindParam(":id", $bookingID);

        $passengerID = $this->getPassengerIDFromBooking($bookingID);
        $this->tokenService->send_gcm_notify($passengerID, "Booking Request Accepted", "Your booking request is accepted!", true);

        if ($stmt->execute()) {
            $response = new Response();
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Booking Confirmed!";
            return $response;
        } else {
            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An unexpected error has occured while confirming the booking. Please try again!";
            return $response;
        }
    }
    public function deleteRideRequest($bookingID)
    {
        $approveBookingQuery = "UPDATE " . Constants::BOOKINGS_TABLE . " SET
        status = :status WHERE id = :id";

        $stmt = $this->conn->prepare($approveBookingQuery);

        $bookingStatus = "declined";

        $stmt->bindParam(":status", $bookingStatus);
        $stmt->bindParam(":id", $bookingID);

        if ($stmt->execute()) {
            $passengerID = $this->getPassengerIDFromBooking($bookingID);
            $this->tokenService->send_gcm_notify($passengerID, "Booking Request Rejected", "Your booking request is rejected!", true);

            $response = new Response();
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Booking declined!";
            return $response;
        } else {
            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An unexpected error has occured while declining the booking. Please try again!";
            return $response;
        }
    }

    public function signIn($driverID)
    {
        $approveBookingQuery = "UPDATE " . Constants::DRIVERS_TABLE . " SET
        signed_in = :signed_in WHERE id = :id";

        $stmt = $this->conn->prepare($approveBookingQuery);

        $signIn = true;

        $stmt->bindParam(":signed_in", $signIn);
        $stmt->bindParam(":id", $driverID);

        $stmt->execute();
        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "1";
        return $response;
    }

    public function signOff($driverID)
    {
        $approveBookingQuery = "UPDATE " . Constants::DRIVERS_TABLE . " SET
        signed_in = :signed_in WHERE id = :id";

        $stmt = $this->conn->prepare($approveBookingQuery);

        $signIn = false;

        $stmt->bindParam(":signed_in", $signIn);
        $stmt->bindParam(":id", $driverID);

        $stmt->execute();
        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "0";
        return $response;
    }


    public function checkSignInStatus($driverID)
    {
        $checkSignInQuery = "SELECT signed_in from " . Constants::DRIVERS_TABLE . " WHERE id = :id";

        $stmt = $this->conn->prepare($checkSignInQuery);

        $stmt->bindParam(":id", $driverID);

        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = $row['signed_in'];
        return $response;
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

    public function getPassengerIDFromBooking($bookingID)
    {
        $bookingQuery = "SELECT * FROM " . Constants::BOOKINGS_TABLE . " WHERE
                            id = :id";

        $stmt = $this->conn->prepare($bookingQuery);

        $stmt->bindParam(":id", $bookingID);

        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        return $row['passenger_id'];
    }
}
