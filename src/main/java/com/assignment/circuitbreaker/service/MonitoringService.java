package com.assignment.circuitbreaker.service;

import com.assignment.circuitbreaker.circuitBreaker.CircuitBreaker;
import com.assignment.circuitbreaker.circuitBreaker.Foo;
import com.assignment.circuitbreaker.exception.RemoteServiceException;

/**
 * The service class which makes local and remote calls Uses {@link Foo} object to
 * ensure remote calls don't use up resources.
 */
public class MonitoringService {

    private final CircuitBreaker delayedService;

    private final CircuitBreaker quickService;

    public MonitoringService(CircuitBreaker delayedService, CircuitBreaker quickService) {
        this.delayedService = delayedService;
        this.quickService = quickService;
    }

    public String localResourceResponse() {
        return "Local Service is working";
    }

    public String delayedServiceResponse() {
        try {
            return this.delayedService.attemptRequest();
        } catch (RemoteServiceException e) {
            return e.getMessage();
        }
    }
    public String quickServiceResponse() {
        try {
            return this.quickService.attemptRequest();
        } catch (RemoteServiceException e) {
            return e.getMessage();
        }
    }
}
