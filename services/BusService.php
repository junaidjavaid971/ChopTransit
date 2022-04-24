<?php

use LDAP\Result;

include_once '../models/response.php';
include_once '../models/Response/Bus.php';
include_once '../models/Response/Driver.php';

/*ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);*/

class BusService
{

    // Connection
    private $conn;

    public function __construct($db)
    {
        $this->conn = $db;
    }

    public function addBus($busName, $registrationNumber,  $busType, $color, $assignedTo, $vacancy)
    {
        $addBusQuery = "INSERT INTO " . Constants::BUSES_TABLE . " SET 
                        bus_name = :bus_name,
                        bus_registration_number = :bus_registration_number,
                        bus_type = :bus_type,
                        bus_color = :bus_color,
                        vacancy = :vacancy,
                        assigned_to = :assigned_to";

        $stmt = $this->conn->prepare($addBusQuery);

        $stmt->bindParam(":bus_name", $busName);
        $stmt->bindParam(":bus_registration_number", $registrationNumber);
        $stmt->bindParam(":bus_type", $busType);
        $stmt->bindParam(":bus_color", $color);
        $stmt->bindParam(":vacancy", $vacancy);
        $stmt->bindParam(":assigned_to", $assignedTo);

        $response = new Response();
        if ($stmt->execute()) {
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Bus Registered Successfully!";
            return $response;
        } else {
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An error occured while registering the bus. Please try again!";
            return $response;
        }
    }

    public function getAllBuses()
    {
        $getAllBusesQuery = "SELECT * FROM " . Constants::BUSES_TABLE;
        $stmt = $this->conn->prepare($getAllBusesQuery);

        $stmt->execute();
        $busesList = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $bus = new Bus();

            $bus->id = $row['id'];
            $bus->busName = $row['bus_name'];
            $bus->busRegNo = $row['bus_registration_number'];
            $bus->busType = $row['bus_type'];
            $bus->busColor = $row['bus_color'];
            $bus->vacancy = $row['vacancy'];
            $bus->assignedTo = $row['assigned_to'];

            if (strcmp($bus->assignedTo, "0") != 0) {
                $bus->driver = $this->getDriverProfileByID($bus->assignedTo);
            }

            array_push($busesList, $bus);
        }

        $bus = new Buses();
        $bus->buses = $busesList;
        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Buses List";
        $response->data = $bus;
        return $response;
    }

    public function getAllBusesByID($id)
    {
        $getAllBusesQuery = "SELECT * FROM " . Constants::BUSES_TABLE . " WHERE id = :id";
        $stmt = $this->conn->prepare($getAllBusesQuery);

        $stmt->bindParam(":id", $id);
        $stmt->execute();
        $busesList = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $bus = new Bus();

            $bus->id = $row['id'];
            $bus->busName = $row['bus_name'];
            $bus->busRegNo = $row['bus_registration_number'];
            $bus->busType = $row['bus_type'];
            $bus->busColor = $row['bus_color'];
            $bus->vacancy = $row['vacancy'];
            $bus->assignedTo = $row['assigned_to'];

            if (strcmp($bus->assignedTo, "0") != 0) {
                $bus->driver = $this->getDriverProfileByID($bus->assignedTo);
            }

            array_push($busesList, $bus);
        }

        $bus = new Buses();
        $bus->buses = $busesList;
        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Buses List";
        $response->data = $bus;
        return $response;
    }

    public function getAllBusesByAssignedDriver($assignedDriver)
    {
        $getAllBusesQuery = "SELECT * FROM " . Constants::BUSES_TABLE . " WHERE assigned_to = :assigned_to";
        $stmt = $this->conn->prepare($getAllBusesQuery);

        $stmt->bindParam(":assigned_to", $assignedDriver);
        $stmt->execute();
        $busesList = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $bus = new Bus();

            $bus->id = $row['id'];
            $bus->busName = $row['bus_name'];
            $bus->busRegNo = $row['bus_registration_number'];
            $bus->busType = $row['bus_type'];
            $bus->busColor = $row['bus_color'];
            $bus->vacancy = $row['vacancy'];
            $bus->assignedTo = $row['assigned_to'];

            if (strcmp($bus->assignedTo, "0") != 0) {
                $bus->driver = $this->getDriverProfileByID($bus->assignedTo);
            }

            array_push($busesList, $bus);
        }

        $bus = new Buses();
        $bus->buses = $busesList;
        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Buses List";
        $response->data = $bus;
        return $response;
    }

    public function getAllBusesByAssignedRoute($assignedRoute)
    {
        $getAllBusesQuery = "SELECT * FROM " . Constants::BUSES_TABLE . " WHERE assigned_route = :assigned_route";
        $stmt = $this->conn->prepare($getAllBusesQuery);

        $stmt->bindParam(":assigned_route", $assignedRoute);
        $stmt->execute();
        $busesList = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $bus = new Bus();

            $bus->id = $row['id'];
            $bus->busName = $row['bus_name'];
            $bus->busRegNo = $row['bus_registration_number'];
            $bus->busType = $row['bus_type'];
            $bus->busColor = $row['bus_color'];
            $bus->vacancy = $row['vacancy'];
            $bus->assignedTo = $row['assigned_to'];

            if (strcmp($bus->assignedTo, "0") != 0) {
                $bus->driver = $this->getDriverProfileByID($bus->assignedTo);
            }

            array_push($busesList, $bus);
        }

        $bus = new Buses();
        $bus->buses = $busesList;
        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Buses List";
        $response->data = $bus;
        return $response;
    }

    public function updateBus($busName, $registrationNumber, $busType, $color, $assignedTo, $id, $vacancy)
    {
        $updateBusQuery = "UPDATE " . Constants::BUSES_TABLE . " SET 
                            bus_name = :bus_name,
                            bus_registration_number = :bus_registration_number,
                            bus_type = :bus_type,
                            bus_color = :bus_color,
                            assigned_to = :assigned_to,
                            vacancy = :vacancy 
                            WHERE id = :id";

        $stmt = $this->conn->prepare($updateBusQuery);

        $stmt->bindParam(":bus_name", $busName);
        $stmt->bindParam(":bus_registration_number", $registrationNumber);
        $stmt->bindParam(":bus_type", $busType);
        $stmt->bindParam(":bus_color", $color);
        $stmt->bindParam(":vacancy", $vacancy);
        $stmt->bindParam(":assigned_to", $assignedTo);
        $stmt->bindParam(":id", $id);

        $response = new Response();
        if ($stmt->execute()) {
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Bus Updated!";
            return $response;
        } else {
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An error occured while updating the bus. Please try again!";
            return $response;
        }
    }

    public function deleteBus($id)
    {
        $deleteRouteQuery = "DELETE from " . Constants::BUSES_TABLE . "  WHERE id = :id";

        $stmt = $this->conn->prepare($deleteRouteQuery);

        $stmt->bindParam(":id", $id);

        $response = new Response();

        $stmt->execute();

        $this->deleteAssignedRouteContainingBusID($id);

        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Bus Deleted!";

        return $response;
    }

    public function deleteAssignedRouteContainingBusID($busID)
    {
        $deleteQuery = "DELETE from " . Constants::ASSIGNED_ROUTES_TABLE . " WHERE 
                        bus_id = :bus_id";

        $stmt = $this->conn->prepare($deleteQuery);

        $stmt->bindParam(":bus_id", $busID);

        $stmt->execute();
    }


    public function getAssignedBuses()
    {
        $getAllBusesQuery = "SELECT * FROM " . Constants::BUSES_TABLE;
        $stmt = $this->conn->prepare($getAllBusesQuery);

        $stmt->execute();
        $busesList = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $bus = new Bus();

            $bus->id = $row['id'];
            $bus->busName = $row['bus_name'];
            $bus->busRegNo = $row['bus_registration_number'];
            $bus->busType = $row['bus_type'];
            $bus->busColor = $row['bus_color'];
            $bus->vacancy = $row['vacancy'];
            $bus->assignedTo = $row['assigned_to'];

            if (strcmp($bus->assignedTo, "0") != 0) {
                $bus->driver = $this->getDriverProfileByID($bus->assignedTo);
            }

            array_push($busesList, $bus);
        }

        $bus = new Buses();
        $bus->buses = $busesList;
        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Buses List";
        $response->data = $bus;
        return $response;
    }


    public function getDriverProfileByID($id)
    {
        $getDriverQuery = "SELECT * FROM " . Constants::DRIVERS_TABLE . " WHERE id = :id";

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
        $driver->userID = $row['user_id'];

        return $driver;
    }
}
