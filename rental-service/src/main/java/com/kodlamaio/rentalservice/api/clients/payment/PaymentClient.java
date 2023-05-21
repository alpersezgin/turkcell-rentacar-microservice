package com.kodlamaio.rentalservice.api.clients.payment;

import com.kodlamaio.commonpackage.utils.dto.ClientResponse;
import com.kodlamaio.commonpackage.utils.dto.ProcessPaymentRequest;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", fallbackFactory = PaymentClientFallback.class)
public interface PaymentClient {
    @PostMapping(value = "/api/payments/process-payment")
    @Retry(name = "notAvailableService")
    ClientResponse processPaymentRequest(@RequestBody ProcessPaymentRequest request);

}
