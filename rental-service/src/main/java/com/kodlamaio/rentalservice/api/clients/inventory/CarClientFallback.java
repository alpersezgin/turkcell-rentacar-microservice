package com.kodlamaio.rentalservice.api.clients.inventory;

import com.kodlamaio.commonpackage.utils.dto.ClientResponse;
import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class CarClientFallback implements CarClient {
    @Override
    public ClientResponse checkIfCarAvailable(UUID carId) {
        throw new BusinessException("checkIfCarAvailable: INVENTORY_SERVICE_NOT_AVAILABLE_RIGHT_NOW");
    }

    @Override
    public double getDailyPrice(UUID carId) {
        throw new BusinessException("getDailyPrice: INVENTORY_SERVICE_NOT_AVAILABLE_RIGHT_NOW");
    }
}
