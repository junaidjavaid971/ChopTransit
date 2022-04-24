<?php


include_once '../models/response.php';
include_once '../models/Response/Stops.php';
include_once '../models/Response/Route.php';
include_once '../models/Response/Bus.php';
include_once '../models/Response/Driver.php';
include_once '../models/Response/AssignedRoutes.php';
include_once 'TokenService.php';

class AssignRouteService
{
    public $conn;
    public $tokenService;

    public function __construct($db)
    {
        $this->conn = $db;
        $this->tokenService = new TokenService($db);
    }

    public function assignRoute($departureTime, $route, $bus)
    {
        $assignRouteQuery = "INSERT into " . Constants::ASSIGNED_ROUTES_TABLE . " SET
                            departure_time = :departure_time,
                            route_id = :route_id,
                            bus_id = :bus_id";

        $stmt = $this->conn->prepare($assignRouteQuery);

        $stmt->bindParam(":departure_time", $departureTime);
        $stmt->bindParam(":route_id", $route['id']);
        $stmt->bindParam(":bus_id", $bus['id']);

        if ($stmt->execute()) {
            $this->assignStops($this->conn->lastInsertId(), $route);
            $response = new Response();
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Route assigned successfully!";
            return $response;
        } else {
            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An unexpected eror occured while assigning the route. Please try again!";
            return $response;
        }
    }

    public function assignStops($assignedRouteID, $route)
    {
        $stops = new Stops();
        $stops->stops = $route['stops'];

        foreach ($stops->stops as $stop) {

            $assignStopQuery = "INSERT INTO " . Constants::ASSIGNED_STOPS_TABLE . " SET
                            stop_name = :stop_name,
                            stop_Id = :stop_Id,
                            latitude = :latitude,
                            longitude = :longitude,
                            arrival_time = :arrival_time,
                            assigned_route_id = :assigned_route_id";

            $stmt = $this->conn->prepare($assignStopQuery);


            $stmt->bindParam("stop_name", $stop['stopName']);
            $stmt->bindParam("stop_Id", $stop['id']);
            $stmt->bindParam("latitude", $stop['latitude']);
            $stmt->bindParam("longitude", $stop['longitude']);
            $stmt->bindParam("arrival_time", $stop['arrivalTime']);
            $stmt->bindParam("assigned_route_id", $assignedRouteID);

            $stmt->execute();
        }
    }

    public function assignDriverWithBus($busID, $driverID)
    {
        $assignDriverQuery = "UPDATE " . Constants::BUSES_TABLE . " SET
                                assigned_to = :assigned_to
                                WHERE id = :id";

        $stmt = $this->conn->prepare($assignDriverQuery);

        $stmt->bindParam(":assigned_to", $driverID);
        $stmt->bindParam(":id", $busID);

        if ($stmt->execute()) {
            $this->tokenService->send_gcm_notify($driverID, "Route Assigned", "Admin has assigned a new route to you. ", false);

            $response = new Response();
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Driver assigned";
            return $response;
        } else {
            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An unexpected error occured while assigning the driver with this bus. Please try again!";
            return $response;
        }
    }

    public function getAssignedRoutes()
    {
        $assignedRoutesQuery = "SELECT * FROM " .  Constants::ASSIGNED_ROUTES_TABLE;

        $stmt = $this->conn->prepare($assignedRoutesQuery);

        $stmt->execute();

        $assignedRoutesList = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {

            $assignedroute = new AssignedRoute();
            $assignedroute->id = $row['id'];
            $assignedroute->departureTime = $row['departure_time'];
            $assignedroute->route = $this->getRouteOnID($row['route_id'], $assignedroute->id);
            $assignedroute->bus = $this->getBusOnID($row['bus_id']);

            if (strcmp($assignedroute->bus->busName, "") != 0) {
                array_push($assignedRoutesList, $assignedroute);
            }
        }

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Assigned Routes!";
        $response->data = $assignedRoutesList;
        return $response;
    }


    public function getAssignedRoutesByID($id)
    {
        $assignedRoutesQuery = "SELECT * FROM " .  Constants::ASSIGNED_ROUTES_TABLE . " WHERE id = :id";

        $stmt = $this->conn->prepare($assignedRoutesQuery);
        $stmt->bindParam(":id", $id);
        $stmt->execute();

        $assignedRoutesList = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $assignedroute = new AssignedRoute();
            $assignedroute->id = $row['id'];
            $assignedroute->departureTime = $row['departure_time'];
            $assignedroute->route = $this->getRouteOnID($row['route_id'], $assignedroute->id);
            $assignedroute->bus = $this->getBusOnID($row['bus_id']);

            array_push($assignedRoutesList, $assignedroute);
        }

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Assigned Routes!";
        $response->data = $assignedRoutesList;
        return $response;
    }

    public function getAssignedRoutesByDriverID($id)
    {
        $assignedRoutesQuery = "SELECT assigned_routes.* FROM " .  Constants::ASSIGNED_ROUTES_TABLE . " 
                                INNER JOIN " . Constants::BUSES_TABLE . " on assigned_routes.bus_id = bus.id 
                                WHERE bus.assigned_to = :id";

        $stmt = $this->conn->prepare($assignedRoutesQuery);
        $stmt->bindParam(":id", $id);
        $stmt->execute();

        $assignedRoutesList = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $assignedroute = new AssignedRoute();
            $assignedroute->id = $row['id'];
            $assignedroute->departureTime = $row['departure_time'];
            $assignedroute->route = $this->getRouteOnID($row['route_id'], $assignedroute->id);
            $assignedroute->bus = $this->getBusOnID($row['bus_id']);

            array_push($assignedRoutesList, $assignedroute);
        }

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Assigned Routes!";
        $response->data = $assignedRoutesList;
        return $response;
    }


    public function getRouteOnID($id, $assignedRouteID)
    {
        $routesQuery = "SELECT * FROM " . Constants::ROUTES_TABLE . " WHERE id = :id";
        $stmt = $this->conn->prepare($routesQuery);

        $stmt->bindParam(":id", $id);
        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $route = new Route();
        $route->id = $row['id'];
        $route->routeName = $row['route_name'];
        $route->fare = $row['fare'];

        $stops = $this->getAssignedStopOnID($assignedRouteID);

        $route->stops = $stops;

        return $route;
    }


    public function getAssignedStopOnID($id)
    {
        $stopsQuery = "SELECT * FROM " . Constants::ASSIGNED_STOPS_TABLE . " WHERE assigned_route_id = :assigned_route_id";
        $stmt = $this->conn->prepare($stopsQuery);

        $stmt->bindParam(":assigned_route_id", $id);
        $stmt->execute();

        $stops = array();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $stop = new Stop();
            $stop->id = $row['id'];
            $stop->stopName = $row['stop_name'];
            $stop->latitude = $row['latitude'];
            $stop->longitude = $row['longitude'];
            $stop->arrivalTime = $row['arrival_time'];

            array_push($stops, $stop);
        }
        return $stops;
    }


    public function getBusOnID($id)
    {
        $getAllBusesQuery = "SELECT bus.* , driver.* FROM " . Constants::BUSES_TABLE . " INNER JOIN " .
            Constants::DRIVERS_TABLE . " on bus.assigned_to = driver.id WHERE bus.id = :id";
        $stmt = $this->conn->prepare($getAllBusesQuery);

        $stmt->bindParam(":id", $id);
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

        $driver = new Driver();
        $driver->id = $row['assigned_to'];
        $driver->firstName = $row['first_name'];
        $driver->lastName = $row['last_name'];
        $driver->email = $row['email'];
        $driver->phoneNumber = $row['phone_number'];
        $driver->companyID = $row['company_id'];
        $driver->userID = $row['user_id'];

        $bus->driver = $driver;

        return $bus;
    }
}
