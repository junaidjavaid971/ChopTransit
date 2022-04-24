<?php

class Companies
{
    public $companies;

    public function __construct()
    {
        $this->companies = new Company();
    }
}

class Company
{
    public $id;
    public $companyName;
    public $companyContactNumber;
    public $companyEmail;

    public function __construct()
    {
    }
}
