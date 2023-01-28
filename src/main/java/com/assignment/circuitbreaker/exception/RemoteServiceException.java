package com.assignment.circuitbreaker.exception;

public class RemoteServiceException extends Exception{
    public RemoteServiceException(String message) {
        super(message);
    }
}
