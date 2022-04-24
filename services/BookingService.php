<?php

include_once '../models/response.php';
include_once '../models/Response/Stops.php';
include_once '../models/Response/Route.php';
include_once '../models/Response/Bus.php';
include_once '../models/Response/Driver.php';
include_once '../models/Response/Booking.php';
include_once '../models/Response/AssignedRoutes.php';
include_once 'TokenService.php';

class BookingService
{

    public $conn;
    public $tokenService;

    public function __construct($db)
    {
        $this->conn = $db;
        $this->tokenService = new TokenService($db);
    }

    public function placeBooking($passengerID, $assignedRouteID, $stopName, $driverID)
    {
        $assignedroute = $this->getAssignedRoutesByID($assignedRouteID);

        $stop = $assignedroute->route->stops[0];

        $placeBookingQuery = "INSERT INTO " . Constants::BOOKINGS_TABLE . " SET
                                stop_name = :stop_name,
                                passenger_id = :passenger_id,
                                assigned_route_id = :assigned_route_id,
                                status = :status,
                                latitude = :latitude,
                                longitude = :longitude";

        $stmt = $this->conn->prepare($placeBookingQuery);

        $status = "pending";

        $stmt->bindParam(":stop_name", $stopName);
        $stmt->bindParam(":passenger_id", $passengerID);
        $stmt->bindParam(":assigned_route_id", $assignedRouteID);
        $stmt->bindParam(":latitude", $stop->latitude);
        $stmt->bindParam(":longitude", $stop->longitude);
        $stmt->bindParam(":status", $status);

        if ($stmt->execute()) {
            $id = $this->conn->lastInsertID();
            $this->tokenService->send_gcm_notify($passengerID, "Booking Request Sent", "Your booking request is sent to the driver. We will let you know once he approves/rejects it.", true);
            $this->tokenService->send_gcm_notify($driverID, "Booking Request Received", "You have received a new booking request from passenger. Please open the app to check more.", true);

            $response = new Response();
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Your ride request is sent to the driver. He will soon confirm your request!";

            $this->saveQRCode($id);
            $response->data = $this->getBookingByID($id);
            return $response;
        } else {
            $response = new Response();
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "An unexpected error occured while confirming your booking. Please try again!";
            return $response;
        }
    }

    public function getBookingByID($id)
    {
        $bookingQuery = "SELECT * from " . Constants::BOOKINGS_TABLE . " WHERE id = :id";

        $stmt = $this->conn->prepare($bookingQuery);

        $stmt->bindParam(":id", $id);

        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        $booking = new Booking();
        $booking->assignedRoute = $this->getAssignedRoutesByID($row['assigned_route_id']);
        $booking->passenger = $this->getPassengerByID($row['passenger_id']);
        $booking->latitude = $row['latitude'];
        $booking->stopName = $row['stop_name'];
        $booking->longitude = $row['longitude'];
        $booking->status = $row['status'];
        $booking->id = $row['id'];

        return $booking;
    }

    public function saveQRCode($id)
    {
        $qc = new QRCODE();
        $qc->TEXT($id);
        $qc->QRCODE(200, "../qrCodes/booking_" . $id . "_qr_code");
    }


    public function getAllBookings()
    {
        $bookingQuery = "SELECT * from " . Constants::BOOKINGS_TABLE;

        $stmt = $this->conn->prepare($bookingQuery);

        $stmt->execute();

        $bookingsList = array();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $booking = new Booking();
            $booking->assignedRoute = $this->getAssignedRoutesByID($row['assigned_route_id']);
            $booking->passenger = $this->getPassengerByID($row['passenger_id']);
            $booking->stopName = $row['stop_name'];
            $booking->latitude = $row['latitude'];
            $booking->longitude = $row['longitude'];
            $booking->status = $row['status'];
            $booking->id = $row['id'];

            array_push($bookingsList, $booking);
        }

        $bookings = new Bookings();
        $bookings->bookings = $bookingsList;

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Bookings List";
        $response->data = $bookings;
        return $response;
    }

    public function getAssignedRoutesByID($id)
    {
        $assignedRoutesQuery = "SELECT * FROM " .  Constants::ASSIGNED_ROUTES_TABLE . " WHERE id = :id";

        $stmt = $this->conn->prepare($assignedRoutesQuery);
        $stmt->bindParam(":id", $id);
        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $assignedroute = new AssignedRoute();
        $assignedroute->id = $row['id'];
        $assignedroute->departureTime = $row['departure_time'];
        $assignedroute->route = $this->getRouteOnID($row['route_id'], $assignedroute->id);
        $assignedroute->bus = $this->getBusOnID($row['bus_id']);

        return $assignedroute;
    }

    public function getPassengerByID($id)
    {
        $passengerQuery = "SELECT * FROM " .  Constants::PESSENGER_TABLE . " WHERE id = :id";

        $stmt = $this->conn->prepare($passengerQuery);
        $stmt->bindParam(":id", $id);
        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $pessenger = new Pessenger();
        $pessenger->id = $row['id'];
        $pessenger->name = $row['name'];
        $pessenger->email = $row['email'];
        $pessenger->phoneNumber = $row['phone_number'];
        $pessenger->userID = $row['user_id'];

        return $pessenger;
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

    public function updateLocation($latitude, $longitude, $id)
    {
        $updateLocationQuery = "UPDATE "  . Constants::DRIVERS_TABLE . " SET
                                latitude = :latitude,
                                longitude = :longitude
                                WHERE id = :id";

        $stmt = $this->conn->prepare($updateLocationQuery);

        $stmt->bindParam(":latitude", $latitude);
        $stmt->bindParam(":id", $id);
        $stmt->bindParam(":longitude", $longitude);

        $stmt->execute();

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Location updated";
        return $response;
    }

    public function updateBookingStatus($id, $bookingStatus)
    {
        $updateLocationQuery = "UPDATE "  . Constants::BOOKINGS_TABLE . " SET
                                status = :status
                                WHERE id = :id";

        $stmt = $this->conn->prepare($updateLocationQuery);

        $stmt->bindParam(":status", $bookingStatus);
        $stmt->bindParam(":id", $id);

        $stmt->execute();

        $passengerID = $this->getPassengerIDFromBooking($id);
        $this->tokenService->send_gcm_notify($passengerID, "Booking Update", "Your booking request is " . $bookingStatus, true);

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Booking status updated";
        return $response;
    }

    public function finishRide($id, $bookingStatus)
    {
        $updateLocationQuery = "UPDATE "  . Constants::BOOKINGS_TABLE . " SET
                                status = :status
                                WHERE id = :id";

        $stmt = $this->conn->prepare($updateLocationQuery);

        $stmt->bindParam(":status", $bookingStatus);
        $stmt->bindParam(":id", $id);

        $stmt->execute();

        $passengerID = $this->getPassengerIDFromBooking($id);
        $this->tokenService->send_gcm_notify($passengerID, "Trip Ended", "Your trip is ended", true);

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Booking status updated";
        return $response;
    }


    public function getPendingBookings($driverID)
    {
        $pendingBookingsQuery = "SELECT booking.*,
                                 assigned_routes.bus_id,
                                 assigned_routes.route_id,
                                 assigned_routes.departure_time
                                 from " . Constants::BOOKINGS_TABLE . " INNER JOIN " . Constants::ASSIGNED_ROUTES_TABLE .  "
                                on booking.assigned_route_id = assigned_routes.id WHERE booking.status = :status";

        $stmt = $this->conn->prepare($pendingBookingsQuery);

        $status = "pending";
        $stmt->bindParam(":status", $status);

        $stmt->execute();

        $bookingLists = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $booking = new Booking();

            $busID = $row['bus_id'];

            $driverQuery = "SELECT driver.* from " . Constants::DRIVERS_TABLE . " INNER JOIN " . Constants::BUSES_TABLE .
                " on bus.assigned_to = driver.id WHERE  bus.id = :busID";

            $stmtDriver = $this->conn->prepare($driverQuery);

            $stmtDriver->bindParam(":busID", $busID);

            $stmtDriver->execute();
            while ($busRow = $stmtDriver->fetch(PDO::FETCH_ASSOC)) {
                $id = $busRow['id'];
                if (strcmp($driverID, $id) == 0) {
                    $booking->assignedRoute = $this->getAssignedRoutesByID($row['assigned_route_id']);
                    $booking->passenger = $this->getPassengerByID($row['passenger_id']);
                    $booking->stopName = $row['stop_name'];
                    $booking->latitude = $row['latitude'];
                    $booking->longitude = $row['longitude'];
                    $booking->status = $row['status'];
                    $booking->id = $row['id'];

                    array_push($bookingLists, $booking);
                }
            }
        }

        $bookings = new Bookings();
        $bookings->bookings = $bookingLists;

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Pending Bookings";
        $response->data = $bookings;

        return $response;
    }

    public function getConfirmedBookings($driverID)
    {
        $pendingBookingsQuery = "SELECT booking.*,
                                 assigned_routes.bus_id,
                                 assigned_routes.route_id,
                                 assigned_routes.departure_time
                                 from " . Constants::BOOKINGS_TABLE . " INNER JOIN " . Constants::ASSIGNED_ROUTES_TABLE .  "
                                on booking.assigned_route_id = assigned_routes.id WHERE booking.status = :status";

        $stmt = $this->conn->prepare($pendingBookingsQuery);

        $status = "confirmed";
        $stmt->bindParam(":status", $status);

        $stmt->execute();

        $bookingLists = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $booking = new Booking();

            $busID = $row['bus_id'];

            $driverQuery = "SELECT driver.* from " . Constants::DRIVERS_TABLE . " INNER JOIN " . Constants::BUSES_TABLE .
                " on bus.assigned_to = driver.id WHERE  bus.id = :busID";

            $stmtDriver = $this->conn->prepare($driverQuery);

            $stmtDriver->bindParam(":busID", $busID);

            $stmtDriver->execute();
            while ($busRow = $stmtDriver->fetch(PDO::FETCH_ASSOC)) {
                $id = $busRow['id'];
                if (strcmp($driverID, $id) == 0) {
                    $booking->assignedRoute = $this->getAssignedRoutesByID($row['assigned_route_id']);
                    $booking->passenger = $this->getPassengerByID($row['passenger_id']);
                    $booking->stopName = $row['stop_name'];
                    $booking->latitude = $row['latitude'];
                    $booking->longitude = $row['longitude'];
                    $booking->status = $row['status'];
                    $booking->id = $row['id'];

                    array_push($bookingLists, $booking);
                }
            }
        }

        $bookings = new Bookings();
        $bookings->bookings = $bookingLists;

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Confirmed Bookings";
        $response->data = $bookings;

        return $response;
    }

    public function getActiveOrConfirmedBookings($driverID)
    {
        $pendingBookingsQuery = "SELECT booking.*,
                                 assigned_routes.bus_id,
                                 assigned_routes.route_id,
                                 assigned_routes.departure_time
                                 from " . Constants::BOOKINGS_TABLE . " INNER JOIN " . Constants::ASSIGNED_ROUTES_TABLE .  "
                                on booking.assigned_route_id = assigned_routes.id WHERE booking.status = :cstatus OR booking.status = :astatus";

        $stmt = $this->conn->prepare($pendingBookingsQuery);

        $cstatus = "active";
        $astatus = "confirmed";
        $stmt->bindParam(":cstatus", $cstatus);
        $stmt->bindParam(":astatus", $astatus);

        $stmt->execute();

        $bookingLists = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $booking = new Booking();

            $busID = $row['bus_id'];

            $driverQuery = "SELECT driver.* from " . Constants::DRIVERS_TABLE . " INNER JOIN " . Constants::BUSES_TABLE .
                " on bus.assigned_to = driver.id WHERE  bus.id = :busID";

            $stmtDriver = $this->conn->prepare($driverQuery);

            $stmtDriver->bindParam(":busID", $busID);

            $stmtDriver->execute();
            while ($busRow = $stmtDriver->fetch(PDO::FETCH_ASSOC)) {
                $id = $busRow['id'];
                if (strcmp($driverID, $id) == 0) {
                    $booking->assignedRoute = $this->getAssignedRoutesByID($row['assigned_route_id']);
                    $booking->passenger = $this->getPassengerByID($row['passenger_id']);
                    $booking->stopName = $row['stop_name'];
                    $booking->latitude = $row['latitude'];
                    $booking->longitude = $row['longitude'];
                    $booking->status = $row['status'];
                    $booking->id = $row['id'];

                    array_push($bookingLists, $booking);
                }
            }
        }

        $bookings = new Bookings();
        $bookings->bookings = $bookingLists;

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Active Bookings";
        $response->data = $bookings;

        return $response;
    }

    public function getFinishedBookings($driverID)
    {
        $pendingBookingsQuery = "SELECT booking.*,
                                 assigned_routes.bus_id,
                                 assigned_routes.route_id,
                                 assigned_routes.departure_time
                                 from " . Constants::BOOKINGS_TABLE . " INNER JOIN " . Constants::ASSIGNED_ROUTES_TABLE .  "
                                on booking.assigned_route_id = assigned_routes.id WHERE booking.status = :status";

        $stmt = $this->conn->prepare($pendingBookingsQuery);

        $status = "finished";
        $stmt->bindParam(":status", $status);

        $stmt->execute();

        $bookingLists = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $booking = new Booking();

            $busID = $row['bus_id'];

            $driverQuery = "SELECT driver.* from " . Constants::DRIVERS_TABLE . " INNER JOIN " . Constants::BUSES_TABLE .
                " on bus.assigned_to = driver.id WHERE  bus.id = :busID";

            $stmtDriver = $this->conn->prepare($driverQuery);

            $stmtDriver->bindParam(":busID", $busID);

            $stmtDriver->execute();
            while ($busRow = $stmtDriver->fetch(PDO::FETCH_ASSOC)) {
                $id = $busRow['id'];
                if (strcmp($driverID, $id) == 0) {
                    $booking->assignedRoute = $this->getAssignedRoutesByID($row['assigned_route_id']);
                    $booking->passenger = $this->getPassengerByID($row['passenger_id']);
                    $booking->stopName = $row['stop_name'];
                    $booking->latitude = $row['latitude'];
                    $booking->longitude = $row['longitude'];
                    $booking->status = $row['status'];
                    $booking->id = $row['id'];

                    array_push($bookingLists, $booking);
                }
            }
        }

        $bookings = new Bookings();
        $bookings->bookings = $bookingLists;

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Finished Bookings";
        $response->data = $bookings;

        return $response;
    }


    public function getPassengerPendingBookings($passengerID)
    {
        $pendingBookingsQuery = "SELECT booking.*,
                                 assigned_routes.bus_id,
                                 assigned_routes.route_id,
                                 assigned_routes.departure_time
                                 from " . Constants::BOOKINGS_TABLE . " INNER JOIN " . Constants::ASSIGNED_ROUTES_TABLE .  "
                                on booking.assigned_route_id = assigned_routes.id WHERE booking.passenger_id = :passenger_id
                                AND booking.status = :status";

        $stmt = $this->conn->prepare($pendingBookingsQuery);

        $status = "pending";
        $stmt->bindParam(":status", $status);
        $stmt->bindParam(":passenger_id", $passengerID);

        $stmt->execute();

        $bookingLists = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $booking = new Booking();
            $booking->assignedRoute = $this->getAssignedRoutesByID($row['assigned_route_id']);
            $booking->passenger = $this->getPassengerByID($row['passenger_id']);
            $booking->stopName = $row['stop_name'];
            $booking->latitude = $row['latitude'];
            $booking->longitude = $row['longitude'];
            $booking->status = $row['status'];
            $booking->id = $row['id'];

            array_push($bookingLists, $booking);
        }

        $bookings = new Bookings();
        $bookings->bookings = $bookingLists;

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Pending Bookings";
        $response->data = $bookings;

        return $response;
    }

    public function getPassengerConfirmedBookings($passengerID)
    {
        $pendingBookingsQuery = "SELECT booking.*,
                                 assigned_routes.bus_id,
                                 assigned_routes.route_id,
                                 assigned_routes.departure_time
                                 from " . Constants::BOOKINGS_TABLE . " INNER JOIN " . Constants::ASSIGNED_ROUTES_TABLE .  "
                                on booking.assigned_route_id = assigned_routes.id WHERE booking.passenger_id = :passenger_id
                                AND booking.status = :status";

        $stmt = $this->conn->prepare($pendingBookingsQuery);

        $status = "confirmed";
        $stmt->bindParam(":status", $status);
        $stmt->bindParam(":passenger_id", $passengerID);

        $stmt->execute();

        $bookingLists = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $booking = new Booking();
            $booking->assignedRoute = $this->getAssignedRoutesByID($row['assigned_route_id']);
            $booking->passenger = $this->getPassengerByID($row['passenger_id']);
            $booking->stopName = $row['stop_name'];
            $booking->latitude = $row['latitude'];
            $booking->longitude = $row['longitude'];
            $booking->status = $row['status'];
            $booking->id = $row['id'];

            array_push($bookingLists, $booking);
        }

        $bookings = new Bookings();
        $bookings->bookings = $bookingLists;

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Confirmed Bookings";
        $response->data = $bookings;

        return $response;
    }


    public function getPassengerMissedBookings($passengerID)
    {
        $pendingBookingsQuery = "SELECT booking.*,
                                 assigned_routes.bus_id,
                                 assigned_routes.route_id,
                                 assigned_routes.departure_time
                                 from " . Constants::BOOKINGS_TABLE . " INNER JOIN " . Constants::ASSIGNED_ROUTES_TABLE .  "
                                on booking.assigned_route_id = assigned_routes.id WHERE booking.passenger_id = :passenger_id
                                AND booking.status = :status";

        $stmt = $this->conn->prepare($pendingBookingsQuery);

        $status = "missed";
        $stmt->bindParam(":status", $status);
        $stmt->bindParam(":passenger_id", $passengerID);

        $stmt->execute();

        $bookingLists = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $booking = new Booking();
            $booking->assignedRoute = $this->getAssignedRoutesByID($row['assigned_route_id']);
            $booking->passenger = $this->getPassengerByID($row['passenger_id']);
            $booking->stopName = $row['stop_name'];
            $booking->latitude = $row['latitude'];
            $booking->longitude = $row['longitude'];
            $booking->status = $row['status'];
            $booking->id = $row['id'];

            array_push($bookingLists, $booking);
        }

        $bookings = new Bookings();
        $bookings->bookings = $bookingLists;

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Missed Bookings";
        $response->data = $bookings;

        return $response;
    }

    public function getPassengerFinishedBookings($passengerID)
    {
        $pendingBookingsQuery = "SELECT booking.*,
                                 assigned_routes.bus_id,
                                 assigned_routes.route_id,
                                 assigned_routes.departure_time
                                 from " . Constants::BOOKINGS_TABLE . " INNER JOIN " . Constants::ASSIGNED_ROUTES_TABLE .  "
                                on booking.assigned_route_id = assigned_routes.id WHERE booking.passenger_id = :passenger_id
                                AND booking.status = :status";

        $stmt = $this->conn->prepare($pendingBookingsQuery);

        $status = "finished";
        $stmt->bindParam(":status", $status);
        $stmt->bindParam(":passenger_id", $passengerID);

        $stmt->execute();

        $bookingLists = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $booking = new Booking();
            $booking->assignedRoute = $this->getAssignedRoutesByID($row['assigned_route_id']);
            $booking->passenger = $this->getPassengerByID($row['passenger_id']);
            $booking->stopName = $row['stop_name'];
            $booking->latitude = $row['latitude'];
            $booking->longitude = $row['longitude'];
            $booking->status = $row['status'];
            $booking->id = $row['id'];

            array_push($bookingLists, $booking);
        }

        $bookings = new Bookings();
        $bookings->bookings = $bookingLists;

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Missed Bookings";
        $response->data = $bookings;

        return $response;
    }

    public function getPassengerActiveBookings($passengerID)
    {
        $pendingBookingsQuery = "SELECT booking.*,
                                 assigned_routes.bus_id,
                                 assigned_routes.route_id,
                                 assigned_routes.departure_time
                                 from " . Constants::BOOKINGS_TABLE . " INNER JOIN " . Constants::ASSIGNED_ROUTES_TABLE .  "
                                on booking.assigned_route_id = assigned_routes.id WHERE booking.passenger_id = :passenger_id
                                AND booking.status = :status";

        $stmt = $this->conn->prepare($pendingBookingsQuery);

        $status = "active";
        $stmt->bindParam(":status", $status);
        $stmt->bindParam(":passenger_id", $passengerID);

        $stmt->execute();

        $bookingLists = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $booking = new Booking();
            $booking->assignedRoute = $this->getAssignedRoutesByID($row['assigned_route_id']);
            $booking->passenger = $this->getPassengerByID($row['passenger_id']);
            $booking->stopName = $row['stop_name'];
            $booking->latitude = $row['latitude'];
            $booking->longitude = $row['longitude'];
            $booking->status = $row['status'];
            $booking->id = $row['id'];

            array_push($bookingLists, $booking);
        }

        $bookings = new Bookings();
        $bookings->bookings = $bookingLists;

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Missed Bookings";
        $response->data = $bookings;

        return $response;
    }
    public function getPassengerRejectedBookings($passengerID)
    {
        $pendingBookingsQuery = "SELECT booking.*,
                                 assigned_routes.bus_id,
                                 assigned_routes.route_id,
                                 assigned_routes.departure_time
                                 from " . Constants::BOOKINGS_TABLE . " INNER JOIN " . Constants::ASSIGNED_ROUTES_TABLE .  "
                                on booking.assigned_route_id = assigned_routes.id WHERE booking.passenger_id = :passenger_id
                                AND booking.status = :status";

        $stmt = $this->conn->prepare($pendingBookingsQuery);

        $status = "rejected";
        $stmt->bindParam(":status", $status);
        $stmt->bindParam(":passenger_id", $passengerID);

        $stmt->execute();

        $bookingLists = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $booking = new Booking();
            $booking->assignedRoute = $this->getAssignedRoutesByID($row['assigned_route_id']);
            $booking->passenger = $this->getPassengerByID($row['passenger_id']);
            $booking->stopName = $row['stop_name'];
            $booking->latitude = $row['latitude'];
            $booking->longitude = $row['longitude'];
            $booking->status = $row['status'];
            $booking->id = $row['id'];

            array_push($bookingLists, $booking);
        }

        $bookings = new Bookings();
        $bookings->bookings = $bookingLists;

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Rejected Bookings";
        $response->data = $bookings;

        return $response;
    }

    public function getDriverLocation($id)
    {
        $updateLocationQuery = "SELECT latitude , longitude from "  . Constants::DRIVERS_TABLE . " WHERE id = :id";

        $stmt = $this->conn->prepare($updateLocationQuery);

        $stmt->bindParam(":id", $id);

        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        $response = new Response();
        $response->code = ResponseCodes::SUCCESS;
        $response->desc = $row['latitude'] . "|" . $row['longitude'];
        return $response;
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
