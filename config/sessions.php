<?php

class Session
{
    public function __construct()
    {
        session_start();
    }

    public function saveSession($email, $lvl)
    {
        $_SESSION['email'] = $email;
        $_SESSION['lvl'] = $lvl;
    }

    public function getEmail()
    {
        return $_SESSION['email'];
    }

    public function getAccountLevel()
    {
        return $_SESSION['lvl'];
    }

    public function updateAccountLevel($lvl)
    {
        $_SESSION['lvl'] = $lvl;
    }

    public function destorySession()
    {
        session_unset();
        session_destroy();
    }

    public function isLoggedIn()
    {
        return isset($_SESSION['email']);
    }
}
