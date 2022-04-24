<?php

include_once '../models/response.php';
include_once '../models/Response/Company.php';

class CompanyService
{

    // Connection
    public $conn;


    public function __construct($db)
    {
        $this->conn = $db;
    }

    public function addCompany($companyName, $contactNumber, $email)
    {
        $addCompanyQuery = "INSERT INTO " . Constants::COMPANY_TABLE . " SET 
        company_name = :company_name,
        contact_number = :contact_number,
        email = :email";

        $stmt = $this->conn->prepare($addCompanyQuery);

        $stmt->bindParam(":company_name", $companyName);
        $stmt->bindParam(":contact_number", $contactNumber);
        $stmt->bindParam(":email", $email);

        $response = new Response();
        if ($stmt->execute()) {
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Company Added Successfully!";
        } else {
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An error occured while adding the company. Please try again later!";
        }

        return $response;
    }

    public function getAllCompanies()
    {
        $getAllCompaniesQuery = "SELECT * FROM " . Constants::COMPANY_TABLE;

        $stmt = $this->conn->prepare($getAllCompaniesQuery);

        $stmt->execute();
        $companiesList = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $company = new Company();

            $company->id = $row['id'];
            $company->companyName = $row['company_name'];
            $company->companyContactNumber = $row['contact_number'];
            $company->companyEmail = $row['email'];

            array_push($companiesList, $company);
        }

        $companies = new Companies();
        $companies->companies = $companiesList;

        $response = new Response();

        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Companies List!";
        $response->data = $companies;

        return $response;
    }

    public function getCompanyOnID($id)
    {
        $getAllCompaniesQuery = "SELECT * FROM " . Constants::COMPANY_TABLE . " WHERE id = :id";

        $stmt = $this->conn->prepare($getAllCompaniesQuery);

        $stmt->bindParam(":id", $id);
        $stmt->execute();

        $companiesList = array();

        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $company = new Company();

            $company->id = $row['id'];
            $company->companyName = $row['company_name'];
            $company->companyContactNumber = $row['contact_number'];
            $company->companyEmail = $row['email'];

            array_push($companiesList, $company);
        }

        $companies = new Companies();
        $companies->companies = $companiesList;

        $response = new Response();

        $response->code = ResponseCodes::SUCCESS;
        $response->desc = "Companies List!";
        $response->data = $companies;


        return $response;
    }

    public function updateCompany($id, $companyName, $contactNumber, $email)
    {
        $updateCompanyQuery = "UPDATE " . Constants::COMPANY_TABLE . " SET company_name = :company_name,
        contact_number = :contact_number,
        email = :email 
        WHERE id = :id";

        $stmt = $this->conn->prepare($updateCompanyQuery);

        $stmt->bindParam(":id", $id);
        $stmt->bindParam(":company_name", $companyName);
        $stmt->bindParam(":contact_number", $contactNumber);
        $stmt->bindParam(":email", $email);

        if ($stmt->execute()) {
            $response = new Response();
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Company updated!";
            return $response;
        } else {
            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An error occured while updating the company. Please try again!";
            return $response;
        }
    }

    public function deleteCompany($id)
    {
        $deleteCompanyQuery = "DELETE from " . Constants::COMPANY_TABLE . " WHERE id = :id";

        $stmt = $this->conn->prepare($deleteCompanyQuery);
        $stmt->bindParam(":id", $id);

        if ($stmt->execute()) {
            $this->updateDriversAssignedToCompany($id);
            $response = new Response();
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Company deleted!";
            return $response;
        } else {
            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = "An error occured while deleting the company. Please try again!";
            return $response;
        }
    }

    public function updateDriversAssignedToCompany($id)
    {
        $deleteCompanyQuery = "UPDATE " . Constants::DRIVERS_TABLE . " SET 
                                company_id = :company_id WHERE company_id = :id";

        $stmt = $this->conn->prepare($deleteCompanyQuery);
        $companyID = 0;
        $stmt->bindParam(":id", $id);
        $stmt->bindParam(":company_id", $companyID);

        $stmt->execute();
    }
}
