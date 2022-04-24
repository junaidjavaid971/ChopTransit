<?php

include_once '../models/Response/Stops.php';
include_once '../models/Response/Routes.php';

class RouteService
{
    // Connection
    private $conn;

    public function __construct($db)
    {
        $this->conn = $db;
    }

    public function addStop($latitude, $longitude, $stopName)
    {
        $addStopQuery = "INSERT INTO 
        " . Constants::STOPS_TABLE . "
            SET
            latitude = :latitude,
            longitude = :longitude,
            stop_name = :stop_name";

        $stmt = $this->conn->prepare($addStopQuery);

        // bind data
        $stmt->bindParam(":latitude", $latitude);
        $stmt->bindParam(":longitude", $longitude);
        $stmt->bindParam(":stop_name", $stopName);

        if ($stmt->execute()) {
            $response = new Response();
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Stop added successfully!";
            return $response;
        } else {
            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An unexpected error occured while adding the stop. Please try again!";
            return $response;
        }
    }

    public function getAllStops()
    {
        $stopsQuery = "SELECT * FROM " . Constants::STOPS_TABLE;
        $stmt = $this->conn->prepare($stopsQuery);

        $stmt->execute();

        $response = array();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $stop = new Stop();
            $stop->id = $row['id'];
            $stop->stopName = $row['stop_name'];
            $stop->latitude = $row['latitude'];
            $stop->longitude = $row['longitude'];

            array_push($response, $stop);
        }

        $stops = new Stops();
        $stops->stops = $response;

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Stops List";
        $response->data = $stops;
        return $response;
    }

    public function addRoute($routeName, $fare, $stops, $companyID)
    {
        $addRouteQuery = "INSERT INTO " . Constants::ROUTES_TABLE . " SET 
                            route_name = :route_name,
                            fare = :fare,
                            stops = :stops,
                            company_id = :company_id";

        $stmt = $this->conn->prepare($addRouteQuery);

        $stmt->bindParam(":route_name", $routeName);
        $stmt->bindParam(":fare", $fare);
        $stmt->bindParam(":stops", $stops);
        $stmt->bindParam(":company_id", $companyID);

        $response = new Response();

        if ($stmt->execute()) {
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Route Added!";
            return $response;
        } else {
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An unexpected error occured while saving the route!";
            return $response;
        }
    }

    public function getAllRoutes()
    {
        $routesQuery = "SELECT * FROM " . Constants::ROUTES_TABLE;
        $stmt = $this->conn->prepare($routesQuery);

        $stmt->execute();

        $response = array();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $route = new Route();
            $route->id = $row['id'];
            $route->routeName = $row['route_name'];
            $route->fare = $row['fare'];

            $idsArray = explode(',', $row['stops']);

            $stops = array();

            foreach ($idsArray as $id) {
                $stop = $this->getStopOnID($id);
                array_push($stops, $stop);
            }

            $route->stops = $stops;
            array_push($response, $route);
        }

        $routes = new Routes();
        $routes->routes = $response;

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Routes";
        $response->data = $routes;
        return $response;
    }

    public function getStopOnID($id)
    {
        $stopsQuery = "SELECT * FROM " . Constants::STOPS_TABLE . " WHERE id = :id";
        $stmt = $this->conn->prepare($stopsQuery);

        $stmt->bindParam(":id", $id);
        $stmt->execute();

        $response = array();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $stop = new Stop();
        $stop->id = $row['id'];
        $stop->stopName = $row['stop_name'];
        $stop->latitude = $row['latitude'];
        $stop->longitude = $row['longitude'];

        return $stop;
    }

    public function getRouteOnID($id)
    {
        $routesQuery = "SELECT * FROM " . Constants::ROUTES_TABLE . " WHERE id = :id";
        $stmt = $this->conn->prepare($routesQuery);

        $stmt->bindParam(":id", $id);
        $stmt->execute();

        $response = array();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $route = new Route();
            $route->id = $row['id'];
            $route->routeName = $row['route_name'];
            $route->fare = $row['fare'];

            $idsArray = explode(',', $row['stops']);

            $stops = array();

            foreach ($idsArray as $id) {
                $stop = $this->getStopOnID($id);
                array_push($stops, $stop);
            }

            $route->stops = $stops;
            array_push($response, $route);
        }

        $routes = new Routes();
        $routes->routes = $response;

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Routes";
        $response->data = $routes;
        return $response;
    }

    public function deleteStop($id)
    {
        $deleteStopQuery = "DELETE from " . Constants::STOPS_TABLE . "  WHERE id = :id";

        $stmt = $this->conn->prepare($deleteStopQuery);

        $stmt->bindParam(":id", $id);

        $response = new Response();

        if ($stmt->execute()) {
            $this->deleteStopFromRoutes($id);
            $this->deleteStopFromAssignedRoutes($id);
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Stop Deleted!";

            return $response;
        } else {
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An unexpected error occured while deleting the stop. Please try again!";

            return $response;
        }
    }
    public function updateStop($id, $latitude, $longitude, $stopName)
    {
        $updateStopQuery = "UPDATE " . Constants::STOPS_TABLE . " SET
                            latitude = :latitude,
                            longitude = :longitude,
                            stop_name = :stop_name
                             WHERE id = :id";

        $stmt = $this->conn->prepare($updateStopQuery);

        $isApproved = true;

        $stmt->bindParam(":latitude", $latitude);
        $stmt->bindParam(":longitude", $longitude);
        $stmt->bindParam(":stop_name", $stopName);
        $stmt->bindParam(":id", $id);

        $response = new Response();

        $stmt->execute();

        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Stop Updated!";

        return $response;
    }

    public function deleteRoute($id)
    {
        $deleteRouteQuery = "DELETE from " . Constants::ROUTES_TABLE . "  WHERE id = :id";

        $stmt = $this->conn->prepare($deleteRouteQuery);

        $stmt->bindParam(":id", $id);

        $response = new Response();

        $stmt->execute();

        $this->deleteAssignedRouteContainingRouteID($id);

        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Route Deleted!";

        return $response;
    }

    public function deleteAssignedRouteContainingRouteID($routeID)
    {
        $deleteAssignedRouteQuery = "DELETE from " . Constants::ASSIGNED_ROUTES_TABLE . " WHERE 
                                        route_id = :route_id";

        $stmt = $this->conn->prepare($deleteAssignedRouteQuery);

        $stmt->bindParam(":route_id", $routeID);

        $stmt->execute();
    }

    public function updateRoute($routeName, $fare, $stops, $companyID, $routeID)
    {
        $addRouteQuery = "UPDATE " . Constants::ROUTES_TABLE . " SET 
                            route_name = :route_name,
                            fare = :fare,
                            stops = :stops,
                            company_id = :company_id 
                            WHERE id = :id";

        $stmt = $this->conn->prepare($addRouteQuery);

        $stmt->bindParam(":route_name", $routeName);
        $stmt->bindParam(":fare", $fare);
        $stmt->bindParam(":stops", $stops);
        $stmt->bindParam(":company_id", $companyID);
        $stmt->bindParam(":id", $routeID);

        $response = new Response();

        if ($stmt->execute()) {
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Route Updated!";
            return $response;
        } else {
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An unexpected error occured while saving the route!";
            return $response;
        }
    }

    public function deleteStopFromRoutes($id)
    {
        $routesResponse = $this->getStopIDsFromRoutes();

        foreach ($routesResponse as $route) {
            if (str_contains($route->stops, $id)) {

                $ids = explode(',', $route->stops);

                $stopIDs = "";

                foreach ($ids as $rid) {
                    if (strcmp($rid, $id) != 0) {
                        $stopIDs .= $rid . ",";
                    }
                }

                $stopIDs = substr($stopIDs, 0, -1);
                $this->updateStopIDsInRoutes($route->id, $stopIDs);
            }
        }
    }

    public function deleteStopFromAssignedRoutes($id)
    {
        $deleteStopQuery = "DELETE from " . Constants::ASSIGNED_STOPS_TABLE . "  WHERE stop_id = :stop_id";

        $stmt = $this->conn->prepare($deleteStopQuery);

        $stmt->bindParam(":stop_id", $id);

        $stmt->execute();
    }

    public function updateStopIDsInRoutes($routeID, $stopID)
    {
        $updateRouteQuery = "UPDATE 
                            " . Constants::ROUTES_TABLE . "
                                SET
                                stops = :stops
                                WHERE
                                id = :id";

        $stmt = $this->conn->prepare($updateRouteQuery);

        // bind data
        $stmt->bindParam(":stops", $stopID);
        $stmt->bindParam(":id", $routeID);

        $stmt->execute();
    }

    public function getStopIDsFromRoutes()
    {
        $routesQuery = "SELECT * FROM " . Constants::ROUTES_TABLE;
        $stmt = $this->conn->prepare($routesQuery);

        $stmt->execute();

        $response = array();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $route = new Route();

            $route->stops = $row['stops'];
            $route->id = $row['id'];
            array_push($response, $route);
        }

        return $response;
    }
}
