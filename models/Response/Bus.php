<?php

class Buses
{
    public $buses;

    public function __construct()
    {
        $this->buses = new Bus();
    }
}

class Bus
{
    public $id;
    public $busName;
    public $busRegNo;
    public $busType;
    public $busColor;
    public $vacancy;
    public $assignedTo;
    public $driver;

    public function __construct()
    {
    }
}
