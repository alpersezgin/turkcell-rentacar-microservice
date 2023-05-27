package com.kodlamaio.invoiceservice.api.clients.inventory;

import com.kodlamaio.commonpackage.utils.dto.responses.GetCarDetailsResponse;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "inventory-service", fallbackFactory = CarClientFallback.class)
public interface CarClient {
    @GetMapping(value = "/api/cars/details/{id}")
    @Retry(name = "notAvailableService")
    GetCarDetailsResponse getDetails(@PathVariable UUID id);
}
