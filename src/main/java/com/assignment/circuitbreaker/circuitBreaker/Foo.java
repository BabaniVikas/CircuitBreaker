package com.assignment.circuitbreaker.circuitBreaker;

import com.assignment.circuitbreaker.exception.RemoteServiceException;
import com.assignment.circuitbreaker.service.RemoteService;

public class Foo implements CircuitBreaker{
    public int failureCount;
    private final int failureThreshold;
    private final int futureTime = 1000 * 1000 * 1000 * 1000;
    private String lastFailureResponse;
    public long lastFailureTime;
    private long retryTimePeriod;
    private final RemoteService service;
    private State state;
    private final long timeout;
    public Foo(RemoteService serviceToCall, long timeout, int failureThreshold,
                          long retryTimePeriod) {
        this.service = serviceToCall;
        this.state = State.CLOSED;
        this.failureThreshold = failureThreshold;
        this.timeout = timeout;
        this.retryTimePeriod = retryTimePeriod;
        this.lastFailureTime = System.nanoTime() + futureTime;
        this.failureCount = 0;
    }

    @Override
    public void recordSuccess() {
        this.failureCount = 0;
        this.lastFailureTime = System.nanoTime() + futureTime;
        this.state = State.CLOSED;
    }

    @Override
    public void recordFailure(String response) {
        failureCount = failureCount + 1;
        this.lastFailureTime = System.nanoTime();
        this.lastFailureResponse = response;
    }

    protected void evaluateState() {
        if (failureCount >= failureThreshold) { //Then something is wrong with remote service
            if ((System.nanoTime() - lastFailureTime) > retryTimePeriod) {
                state = State.HALF_OPEN;
            } else {
                state = State.OPEN;
            }
        } else {
            state = State.CLOSED;
        }
    }

    @Override
    public String getState() {
        evaluateState();
        return state.name();
    }

    @Override
    public void setState(State state) {
        this.state = state;
        switch (state) {
            case OPEN:
                this.failureCount = failureThreshold;
                this.lastFailureTime = System.nanoTime();
                break;
            case HALF_OPEN:
                this.failureCount = failureThreshold;
                this.lastFailureTime = System.nanoTime() - retryTimePeriod;
                break;
            default:
                this.failureCount = 0;
        }
    }

    @Override
    public String attemptRequest() throws RemoteServiceException {
        evaluateState();
        if (state == State.OPEN) {
            return this.lastFailureResponse;
        } else {
            try {
                var response = service.call();
                recordSuccess();
                return response;
            } catch (RemoteServiceException ex) {
                recordFailure(ex.getMessage());
                throw ex;
            }
        }
    }
}
