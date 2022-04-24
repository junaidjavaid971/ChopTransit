<?php

include_once 'Pessenger.php';
include_once 'AssignedRoutes.php';

class Bookings {
    public $bookings;

    public function __construct()
    {
        $this->bookings = new Booking();
    }
}

class Booking {
    public $id;
    public $passenger;
    public $stopName;
    public $assignedRoute;
    public $latitude;
    public $longitude;
    public $status;

    public function __construct()
    {
        $passenger = new Pessenger();
        $assignedRoute = new AssignedRoute();
    }
}