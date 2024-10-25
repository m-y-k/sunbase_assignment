package com.project.customer.exception;

public class CustomerAlreadyExistsException extends Exception{

    public CustomerAlreadyExistsException(String message) {
        super(message);
    }
}
