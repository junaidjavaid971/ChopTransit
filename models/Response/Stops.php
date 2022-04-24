<?php
class Stops
{
    public $stops;

    public function __construct()
    {
        $this->stops = new Stop();
    }
}

class Stop
{
    public $id;
    public $stopName;
    public $latitude;
    public $longitude;
    public $arrivalTime;

    public function __construct()
    {
    }
}
