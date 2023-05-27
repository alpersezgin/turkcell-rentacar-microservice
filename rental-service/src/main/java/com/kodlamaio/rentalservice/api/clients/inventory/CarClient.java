package com.kodlamaio.rentalservice.api.clients.inventory;

import com.kodlamaio.commonpackage.utils.dto.responses.ClientResponse;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "inventory-service", fallback = CarClientFallback.class)
public interface CarClient {
    @GetMapping(value = "/api/cars/check-car-available/{carId}")
    @Retry(name = "notAvailableService")
    ClientResponse checkIfCarAvailable(@PathVariable UUID carId);

    @GetMapping(value = "/api/cars/daily-price/{carId}")
    @Retry(name = "notAvailableService")
    double getDailyPrice(@PathVariable UUID carId);

}
