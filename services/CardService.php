<?php

use Stripe\Customer;

require '../vendor/stripe/stripe-php/init.php';
include_once '../models/response.php';
include_once '../models/Response/Card.php';
include_once '../models/Response/AlipayResponse.php';
include_once '../models/Response/UserAuthService.php';

class CardService
{
    public $conn;

    public $cardNumber;
    public $cardHolderName;
    public $expiryMonth;
    public $expiryYear;
    public $cvc;

    public $email;
    public $customerID;
    public $cardID;
    public $id;

    public function __construct($db)
    {
        $this->conn = $db;
    }

    public function createCustomer()
    {
        \Stripe\Stripe::setApiKey(Constants::sk_test);
        $stripe = new \Stripe\StripeClient(Constants::sk_test);

        try {
            $token = \Stripe\Token::create([
                'card' => [
                    'number' => $this->cardNumber,
                    'exp_month' => $this->expiryMonth,
                    'exp_year' => $this->expiryYear,
                    'cvc' => $this->cvc,
                    'name' => $this->cardHolderName
                ],
            ]);

            $customer = \Stripe\Customer::create([
                'email' => $this->email,
                'name' => $this->cardHolderName
            ]);

            $card = $stripe->customers->createSource(
                $customer->id,
                ['source' => $token->id]
            );

            echo json_encode($this->updateCustomerIDForPassenger($customer->id, $card->id));
        } catch (Exception $e) {

            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = $e->getMessage();

            echo json_encode($response);
        }
    }

    public function updateCustomerIDForPassenger($customerID, $cardID)
    {

        $updatePassengerQuery = "UPDATE " . Constants::PESSENGER_TABLE . " 
                                SET customer_id = :customer_id ,
                                card_id = :card_id 
                                WHERE id = :id";

        $stmt = $this->conn->prepare($updatePassengerQuery);

        $stmt->bindParam(":customer_id", $customerID);
        $stmt->bindParam(":card_id", $cardID);
        $stmt->bindParam(":id", $this->id);

        if ($stmt->execute()) {
            $response = new Response();
            $response->desc = "Payment details saved for future transactions!";
            $response->code = ResponseCodes::SUCCESS;
            return $response;
        } else {
            $response = new Response();
            $response->desc = "An unexpected error occured while saving the payment details. Please try again!";
            $response->code = ResponseCodes::FAILURE;
            return $response;
        }
    }

    public function performTransaction($amount)
    {
        \Stripe\Stripe::setApiKey(Constants::sk_test);

        try {
            $charge = \Stripe\Charge::create([
                'amount' => $amount,
                'currency' => 'hkd',
                'customer' => $this->customerID
            ]);

            $chargeID = $charge->id;

            //Store Charge ID in DB

            $paymentQuery = "INSERT INTO " . Constants::PAYMENT_TABLE . " SET
                        charge_id = :charge_id,
                        customer_id = :customer_id,
                        passenger_id = :passenger_id";

            $stmt = $this->conn->prepare($paymentQuery);

            $stmt->bindParam(":charge_id", $chargeID);
            $stmt->bindParam(":customer_id", $this->customerID);
            $stmt->bindParam(":passenger_id", $this->id);

            $stmt->execute();

            $response = new Response();
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Transaction Successful";
            return $response;
        } catch (Exception $e) {
            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = $e->getMessage();
            return $response;
        }
    }

    public function getAllCustomers()
    {
        try {
            \Stripe\Stripe::setApiKey(Constants::sk_test);
            echo \Stripe\Customer::all(['limit' => 100000]);
        } catch (Exception $e) {
            echo $e->getMessage();
        }
    }

    public function deleteCustomer()
    {
        $stripe = new \Stripe\StripeClient(Constants::sk_test);
        try {
            $stripe->customers->delete($this->customerID, []);

            $deleteCustomerQuery = "UPDATE " . Constants::PESSENGER_TABLE . " SET
                                customer_id = :customer_id,
                                card_id = :card_id
                                WHERE id = :id";

            $stmt = $this->conn->prepare($deleteCustomerQuery);

            $cid = "";
            $tid = "";

            $stmt->bindParam(":customer_id", $cid);
            $stmt->bindParam(":card_id", $tid);
            $stmt->bindParam(":id", $this->id);

            if ($stmt->execute()) {
                $response = new Response();
                $response->code  = ResponseCodes::SUCCESS;
                $response->desc  = "Payment Method Deleted Successfully!";
                return $response;
            } else {
                $response = new Response();
                $response->code  = ResponseCodes::FAILURE;
                $response->desc  = "An unexpected error occured while deleting the payment method!";
                return $response;
            }
        } catch (Exception $e) {
            $response = new Response();
            $response->code  = ResponseCodes::FAILURE;
            $response->desc  = $e->getMessage();
            return $response;
        }
    }

    public function getCardDetails()
    {
        try {
            $stripe = new \Stripe\StripeClient(Constants::sk_test);
            $stripeCard =  $stripe->customers->retrieveSource($this->customerID, $this->cardID, []);

            $card = new Card();
            $card->id = $stripeCard->id;
            $card->brand = $stripeCard->brand;
            $card->country = $stripeCard->country;
            $card->expiryMonth = $stripeCard->expiryMonth;
            $card->customer = $stripeCard->customer;
            $card->cvc_check = $stripeCard->cvc_check;
            $card->last4 = $stripeCard->last4;
            $card->funding = $stripeCard->funding;
            $card->exp_year = $stripeCard->exp_year;
            $card->exp_month = $stripeCard->exp_month;
            $card->fingerprint = $stripeCard->fingerprint;

            $response = new Response();
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Card Detais";
            $response->data = $card;

            return $response;
        } catch (Exception $e) {
            $response = new Response();
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = $e->getMessage();

            return $response;
        }
    }

    public function updateCustomerCard()
    {
        \Stripe\Stripe::setApiKey(Constants::sk_test);
        $stripe = new \Stripe\StripeClient(Constants::sk_test);
        try {
            $stripe->customers->deleteSource($this->customerID, $this->cardID, []);

            $token = \Stripe\Token::create([
                'card' => [
                    'number' => $this->cardNumber,
                    'exp_month' => $this->expiryMonth,
                    'exp_year' => $this->expiryYear,
                    'cvc' => $this->cvc,
                    'name' => $this->cardHolderName
                ],
            ]);

            $card = $stripe->customers->createSource(
                $this->customerID,
                ['source' => $token->id]
            );

            $response = $this->updateCustomerIDForPassenger($this->customerID, $card->id);

            if (strcmp($response->code, ResponseCodes::SUCCESS) == 0) {
                $response->desc = "Card Upadted";
            }

            return $response;
        } catch (Exception $e) {
            echo $e->getMessage();
        }
    }

    public function deleteCustomerCard()
    {
        \Stripe\Stripe::setApiKey(Constants::sk_test);
        $stripe = new \Stripe\StripeClient(Constants::sk_test);
        try {
            $stripe->customers->deleteSource($this->customerID, $this->cardID, []);

            $cardID = "";
            $response = $this->updateCustomerIDForPassenger($this->customerID, $cardID);


            return $response;
        } catch (Exception $e) {
            echo $e->getMessage();
        }
    }

    public function createAliPayIntent($amount)
    {
        try {
            \Stripe\Stripe::setApiKey(Constants::sk_test);
            $stripe = new \Stripe\StripeClient(Constants::sk_test);

            $paymentIntent = $stripe->paymentIntents->create(
                ['payment_method_types' => ['alipay'], 'amount' => $amount, 'currency' => 'hkd']
            );

            $aliPayResponse = new AlipayResponse();
            $aliPayResponse->paymentIntent = $paymentIntent->id;
            $aliPayResponse->clientSecret = $paymentIntent->client_secret;

            $response = new Response();
            $response->code = ResponseCodes::SUCCESS;
            $response->desc = "Payment Intent Created!";
            $response->data = $aliPayResponse;
            return $response;
        } catch (Exception $e) {
            $response = new Response();
            $response->code = ResponseCodes::FAILURE;
            $response->desc = $e->getMessage();
            return $response;
        }
    }
}
