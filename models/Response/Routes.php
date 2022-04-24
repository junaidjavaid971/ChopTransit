<?php
class Routes
{
    public $routes;
    public function __construct()
    {
        $this->routes = new Route();
    }
}

class Route
{
    public $id;
    public $routeName;
    public $fare;
    public $stops;

    public function __construct()
    {
    }
}
