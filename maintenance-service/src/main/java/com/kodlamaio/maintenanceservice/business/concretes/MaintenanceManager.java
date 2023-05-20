package com.kodlamaio.maintenanceservice.business.concretes;

import com.kodlamaio.commonpackage.configuration.mappers.ModelMapperService;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceCreatedEvent;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceDeletedEvent;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceUpdatedEvent;
import com.kodlamaio.commonpackage.kafka.producer.KafkaProducer;
import com.kodlamaio.maintenanceservice.business.abstracts.MaintenanceService;
import com.kodlamaio.maintenanceservice.business.dto.requests.create.CreateMaintenanceRequest;
import com.kodlamaio.maintenanceservice.business.dto.requests.update.UpdateMaintenanceRequest;
import com.kodlamaio.maintenanceservice.business.dto.responses.create.CreateMaintenanceResponse;
import com.kodlamaio.maintenanceservice.business.dto.responses.get.GetAllMaintenancesResponse;
import com.kodlamaio.maintenanceservice.business.dto.responses.get.GetMaintenanceResponse;
import com.kodlamaio.maintenanceservice.business.dto.responses.update.UpdateMaintenanceResponse;
import com.kodlamaio.maintenanceservice.business.rules.MaintenanceBusinessRules;
import com.kodlamaio.maintenanceservice.entities.Maintenance;
import com.kodlamaio.maintenanceservice.repository.MaintenanceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MaintenanceManager implements MaintenanceService {
    final MaintenanceRepository repository;
    private final ModelMapperService mapper;
    private final MaintenanceBusinessRules rules;
    private final KafkaProducer producer;

    @Override
    public List<GetAllMaintenancesResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(maintenance -> mapper.forResponse().map(maintenance, GetAllMaintenancesResponse.class))
                .toList();
    }

    @Override
    public GetMaintenanceResponse getById(UUID id) {
        rules.checkIfMaintenanceExists(id);
        var maintenance = repository.findById(id).orElseThrow();
        return mapper.forResponse().map(maintenance, GetMaintenanceResponse.class);
    }

    @Override
    public CreateMaintenanceResponse add(CreateMaintenanceRequest request) {
        rules.ensureCarIsAvailable(request.getCarId());

        var maintenance = mapper.forRequest().map(request, Maintenance.class);
        maintenance.setId(UUID.randomUUID());
        var savedMaintenance = repository.save(maintenance);
        sendMaintenanceCreatedEvent(savedMaintenance.getCarId());

        return mapper.forResponse().map(savedMaintenance, CreateMaintenanceResponse.class);
    }

    @Override
    public UpdateMaintenanceResponse update(UUID id, UpdateMaintenanceRequest request) {
        rules.checkIfMaintenanceExists(id);

        var maintenance = mapper.forRequest().map(request, Maintenance.class);
        maintenance.setId(id);
        var updatedMaintenance = repository.save(maintenance);
        sendMaintenanceUpdateEvent(updatedMaintenance.getCarId(), updatedMaintenance);

        return mapper.forResponse().map(updatedMaintenance, UpdateMaintenanceResponse.class);
    }

    @Override
    public void deleteById(UUID id) {
        rules.checkIfMaintenanceExists(id);
        var carId = repository.findById(id).orElseThrow().getCarId();
        repository.deleteById(id);
        sendMaintenanceDeletedEvent(carId);
    }

    private void sendMaintenanceCreatedEvent(UUID carId) {
        var event = new MaintenanceCreatedEvent(carId);
        producer.sendMessage(event, "maintenance-created");
    }

    private void sendMaintenanceDeletedEvent(UUID carId) {
        var event = new MaintenanceDeletedEvent(carId);
        producer.sendMessage(event, "maintenance-deleted");
    }

    private void sendMaintenanceUpdateEvent(UUID carId, Maintenance maintenance) {
        var event = mapper.forResponse().map(maintenance, MaintenanceUpdatedEvent.class);
        event.setCarId(carId);
        producer.sendMessage(event, "maintenance-updated");
    }
}
