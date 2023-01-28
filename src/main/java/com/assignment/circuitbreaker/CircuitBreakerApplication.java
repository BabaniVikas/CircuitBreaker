package com.assignment.circuitbreaker;

import com.assignment.circuitbreaker.circuitBreaker.Foo;
import com.assignment.circuitbreaker.service.DelayedRemoteService;
import com.assignment.circuitbreaker.service.MonitoringService;
import com.assignment.circuitbreaker.service.QuickRemoteService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CircuitBreakerApplication {

    public static void main(String[] args) {
        var serverStartTime = System.nanoTime();

        var delayedService = new DelayedRemoteService(serverStartTime, 5);
        var delayedServiceCircuitBreaker = new Foo(delayedService, 3000, 2,
                2000 * 1000 * 1000);

        var quickService = new QuickRemoteService();
        var quickServiceCircuitBreaker = new Foo(quickService, 3000, 2,
                2000 * 1000 * 1000);

        var monitoringService = new MonitoringService(delayedServiceCircuitBreaker,
                quickServiceCircuitBreaker);

        log.info(monitoringService.localResourceResponse());
        log.info(monitoringService.delayedServiceResponse());
        log.info(monitoringService.delayedServiceResponse());
        log.info(delayedServiceCircuitBreaker.getState());
        log.info(monitoringService.quickServiceResponse());
        log.info(quickServiceCircuitBreaker.getState());

        try {
            log.info("Waiting for delayed service to become responsive");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info(delayedServiceCircuitBreaker.getState());
        log.info(monitoringService.delayedServiceResponse());
        log.info(delayedServiceCircuitBreaker.getState());
    }

}
