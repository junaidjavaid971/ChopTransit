<?php

include_once 'Routes.php';
include_once 'Bus.php';

class AssignedRoutes
{
    public $assignedRoutes;

    public function __construct()
    {
        $this->assignedRoutes = new AssignedRoute();
    }
}

class AssignedRoute
{
    public $route;
    public $bus;
    public $departureTime;
    public $id;

    public function __construct()
    {
        $this->route = new Route();
        $this->bus = new Bus();
    }
}
