package com.kodlamaio.invoiceservice.api.clients.inventory;

import com.kodlamaio.commonpackage.utils.dto.responses.GetCarDetailsResponse;
import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class CarClientFallback implements FallbackFactory<CarClient> {
    @Override
    public CarClient create(Throwable cause) {
        return new CarClient() {
            @Override
            public GetCarDetailsResponse getCarDetails(UUID id) {
                throw new BusinessException("GetCarDetailsResponse: INVENTORY_SERVICE_NOT_AVAILABLE_RIGHT_NOW. Cause: " + cause.getMessage());
            }
        };
    }
}
