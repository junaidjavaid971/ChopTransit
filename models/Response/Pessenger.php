<?php

class Pessengers
{
    public $pessengers;

    public function __construct()
    {
        $this->pessengers = new Pessenger();
    }
}

class Pessenger
{
    // Columns
    public $id;
    public $name;
    public $email;
    public $userID;
    public $phoneNumber;
    public $customerID;
    public $cardID;

    public function __construct()
    {
    }
}
