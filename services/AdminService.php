<?php

include_once '../enums/constants.php';

class AdminService
{

    // Connection
    public $conn;

    public function __construct($db)
    {
        $this->conn = $db;
    }

    public function getCounts()
    {
        $count = $this->getBookingsCount() . "|" . $this->getRoutesCount() . "|" . $this->getDriversCount()
            . "|" . $this->getPassengersCount() . "|" . $this->getCompaniesCount();

        return $count;
    }

    public function getBookingsCount()
    {
        $bookingQuery = "SELECT COUNT(*) AS `count` from " . Constants::BOOKINGS_TABLE;
        $stmt = $this->conn->prepare($bookingQuery);

        $stmt->execute();
        
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        return $row['count'];
    }

    public function getPassengersCount()
    {
        $passengerCountQuery = "SELECT COUNT(*) AS `count` from " . Constants::PESSENGER_TABLE;
        $stmt = $this->conn->prepare($passengerCountQuery);

        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        return $row['count'];
    }

    public function getDriversCount()
    {
        $driverQuery = "SELECT COUNT(*) AS `count` from " . Constants::PESSENGER_TABLE;
        $stmt = $this->conn->prepare($driverQuery);

        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        return $row['count'];
    }

    public function getCompaniesCount()
    {
        $companiesQuery = "SELECT COUNT(*) AS `count` FROM " . Constants::COMPANY_TABLE;

        $stmt = $this->conn->prepare($companiesQuery);

        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        return $row['count'];
    }

    public function getRoutesCount()
    {
        $routesQuery = "SELECT COUNT(*) AS `count` FROM " . Constants::ASSIGNED_ROUTES_TABLE;

        $stmt = $this->conn->prepare($routesQuery);

        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        return $row['count'];
    }
}
