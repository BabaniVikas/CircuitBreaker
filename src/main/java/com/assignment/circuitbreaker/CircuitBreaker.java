package com.assignment.circuitbreaker;

public interface CircuitBreaker {
    public String attemptRequest() throws RemoteServiceException;

    public String getState();

    public void recordFailure(String response);

    public void recordSuccess();

    public void setState(State state);
}
