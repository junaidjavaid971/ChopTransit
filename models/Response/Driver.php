<?php

class Drivers
{
    public $drivers;

    public function __construct()
    {
        $this->drivers = new Driver();
    }
}

class  Driver
{
    public $id;
    public $firstName;
    public $lastName;
    public $email;
    public $phoneNumber;
    public $companyID;
    public $companyName;
    public $isApproved;
    public $userID;
    public $signedIn;
    public $busName;
    public $busRegNo;

    public function __construct()
    {
    }
}
