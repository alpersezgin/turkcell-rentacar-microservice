package com.kodlamaio.filterservice.business.kafka.consumer;

import com.kodlamaio.commonpackage.events.maintenance.MaintenanceCreatedEvent;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceDeletedEvent;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceUpdatedEvent;
import com.kodlamaio.filterservice.business.abstracts.FilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceConsumer {
    private final FilterService service;

    @KafkaListener(
            topics = "maintenance-created",
            groupId = "filter-maintenance-create"
    )
    public void consume(MaintenanceCreatedEvent event) {
        var filter = service.getByCarIad(event.getCarId());
        filter.setState("Maintenance");
        service.add(filter);
        log.info("Maintenance created event consumed{}", event);
    }

    @KafkaListener(
            topics = "maintenance-deleted",
            groupId = "filter-maintenance-delete"
    )
    public void consume(MaintenanceDeletedEvent event) {
        var filter = service.getByCarIad(event.getCarId());
        filter.setState("Available");
        service.add(filter);
        log.info("Maintenance deleted event consumed{}", event);
    }

    @KafkaListener(
            topics = "maintenance-updated",
            groupId = "filter-maintenance-update"
    )
    public void consume(MaintenanceUpdatedEvent event) {
        log.info("Maintenance updated event consumed{}", event);
    }
}