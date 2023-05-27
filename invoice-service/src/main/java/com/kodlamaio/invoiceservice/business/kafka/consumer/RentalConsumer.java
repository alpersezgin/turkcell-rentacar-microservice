package com.kodlamaio.invoiceservice.business.kafka.consumer;

import com.kodlamaio.commonpackage.events.rental.RentalPaymentCompletedEvent;
import com.kodlamaio.invoiceservice.business.abstracts.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalConsumer {
    private final InvoiceService service;

    @KafkaListener(
            topics = "rental-payment-completed",
            groupId = "rental-payment-complete"
    )
    public void consume(RentalPaymentCompletedEvent event) {
        service.addByRentalPaymentCompletedEvent(event);
        log.info("Rental payment completed event consumed{}", event);
    }
}
