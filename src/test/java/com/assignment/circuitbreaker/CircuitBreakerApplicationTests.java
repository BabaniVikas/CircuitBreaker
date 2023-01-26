package com.assignment.circuitbreaker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CircuitBreakerApplicationTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerApplicationTests.class);
    private static final int STARTUP_DELAY = 4;

    private static final int FAILURE_THRESHOLD = 1;

    private static final int RETRY_PERIOD = 2;

    private MonitoringService monitoringService;

    private CircuitBreaker delayedServiceCircuitBreaker;

    private CircuitBreaker quickServiceCircuitBreaker;

    @BeforeEach
    void setupCircuitBreakers() {
        var delayedService = new DelayedRemoteService(System.nanoTime(), STARTUP_DELAY);

        delayedServiceCircuitBreaker = new DefaultCircuitBreaker(delayedService, 3000,
                FAILURE_THRESHOLD,
                RETRY_PERIOD * 1000 * 1000 * 1000);

        var quickService = new QuickRemoteService();

        quickServiceCircuitBreaker = new DefaultCircuitBreaker(quickService, 3000, FAILURE_THRESHOLD,
                RETRY_PERIOD * 1000 * 1000 * 1000);

        monitoringService = new MonitoringService(delayedServiceCircuitBreaker,
                quickServiceCircuitBreaker);
    }

    @Test
    void testFailure_OpenStateTransition() {
        assertEquals("Delayed service is down", monitoringService.delayedServiceResponse());
        assertEquals("OPEN", delayedServiceCircuitBreaker.getState());
        assertEquals("Delayed service is down", monitoringService.delayedServiceResponse());

        assertEquals("Quick Service is working", monitoringService.quickServiceResponse());
        assertEquals("CLOSED", quickServiceCircuitBreaker.getState());

    }

    @Test
    void testFailure_HalfOpenStateTransition() {
        assertEquals("Delayed service is down", monitoringService.delayedServiceResponse());
        assertEquals("OPEN", delayedServiceCircuitBreaker.getState());

        try {
            LOGGER.info("Waiting 2s for delayed service to become responsive");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("HALF_OPEN", delayedServiceCircuitBreaker.getState());
    }

    @Test
    void testRecovery_ClosedStateTransition() {
        assertEquals("Delayed service is down", monitoringService.delayedServiceResponse());
        assertEquals("OPEN", delayedServiceCircuitBreaker.getState());

        try {
            LOGGER.info("Waiting 4s for delayed service to become responsive");
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("HALF_OPEN", delayedServiceCircuitBreaker.getState());
        assertEquals("Delayed service is working", monitoringService.delayedServiceResponse());
        assertEquals("CLOSED", delayedServiceCircuitBreaker.getState());
    }

}
