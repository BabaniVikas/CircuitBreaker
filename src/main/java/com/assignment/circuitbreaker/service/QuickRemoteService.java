package com.assignment.circuitbreaker.service;

import com.assignment.circuitbreaker.exception.RemoteServiceException;

public class QuickRemoteService implements RemoteService {
    @Override
    public String call() throws RemoteServiceException {
        return "Quick Service is working";
    }
}
