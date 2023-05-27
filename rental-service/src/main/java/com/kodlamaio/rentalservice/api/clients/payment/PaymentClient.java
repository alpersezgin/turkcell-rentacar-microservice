package com.kodlamaio.rentalservice.api.clients.payment;

import com.kodlamaio.commonpackage.utils.dto.requests.ProcessPaymentRequest;
import com.kodlamaio.commonpackage.utils.dto.responses.ClientResponse;
import com.kodlamaio.commonpackage.utils.dto.responses.GetPaymentCardHolderResponse;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "payment-service", fallbackFactory = PaymentClientFallback.class)
public interface PaymentClient {
    @PostMapping(value = "/api/payments/process-payment")
    @Retry(name = "notAvailableService")
    ClientResponse processPaymentRequest(@RequestBody ProcessPaymentRequest request);

    @GetMapping("/api/payments/card-holder/{id}")
    @Retry(name = "notAvailableService")
    public GetPaymentCardHolderResponse getCardHolder(@PathVariable UUID id);
}
