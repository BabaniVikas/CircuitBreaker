package com.assignment.circuitbreaker;

public class QuickRemoteService implements RemoteService {
    @Override
    public String call() throws RemoteServiceException {
        return "Quick Service is working";
    }
}
