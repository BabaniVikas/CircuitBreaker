package com.assignment.circuitbreaker;

public interface RemoteService {
    String call() throws RemoteServiceException;
}
