package com.assignment.circuitbreaker.service;

import com.assignment.circuitbreaker.exception.RemoteServiceException;

public interface RemoteService {
    String call() throws RemoteServiceException;
}
