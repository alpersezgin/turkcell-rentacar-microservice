package com.kodlamaio.rentalservice.business.rules;

import com.kodlamaio.commonpackage.utils.dto.responses.ClientResponse;
import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import com.kodlamaio.rentalservice.api.clients.inventory.CarClient;
import com.kodlamaio.rentalservice.repository.RentalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RentalBusinessRules {
    private final RentalRepository repository;
    private final CarClient carClient;

    public void checkIfRentalExists(UUID id) {
        if (!repository.existsById(id)) {
            throw new BusinessException("RENTAL_NOT_EXISTS");
        }
    }

    public void checkCarNotUnderRental(UUID carId) {
        if (repository.existsByCarId(carId)) {
            throw new BusinessException("CAR_ALREADY_UNDER_RENTAL");
        }
    }

    public void ensureCarIsAvailable(UUID carId) {
        ClientResponse clientResponse = carClient.checkIfCarAvailable(carId);
        if (!clientResponse.isSuccess()) {
            throw new BusinessException(clientResponse.getMessage());
        }
    }
}
