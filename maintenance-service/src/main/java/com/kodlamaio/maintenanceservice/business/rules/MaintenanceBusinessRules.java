package com.kodlamaio.maintenanceservice.business.rules;


import com.kodlamaio.commonpackage.utils.dto.responses.ClientResponse;
import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import com.kodlamaio.maintenanceservice.api.clients.CarClient;
import com.kodlamaio.maintenanceservice.repository.MaintenanceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class MaintenanceBusinessRules {
    final MaintenanceRepository repository;
    final CarClient carClient;

    public void checkIfMaintenanceExists(UUID id) {
        if (!repository.existsById(id)) {
            throw new BusinessException("MAINTENANCE_NOT_EXISTS");
        }
    }

    public void ensureCarIsAvailable(UUID carId) {
        ClientResponse clientResponse = carClient.checkIfCarAvailable(carId);
        if (!clientResponse.isSuccess()) {
            throw new BusinessException(clientResponse.getMessage());
        }
    }
}
