package com.kodlamaio.rentalservice.api.clients.payment;

import com.kodlamaio.commonpackage.utils.dto.requests.ProcessPaymentRequest;
import com.kodlamaio.commonpackage.utils.dto.responses.ClientResponse;
import com.kodlamaio.commonpackage.utils.dto.responses.GetPaymentCardHolderResponse;
import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class PaymentClientFallback implements FallbackFactory<PaymentClient> {
    /*@Override
    public ClientResponse processPaymentRequest(ProcessPaymentRequest request) {
        throw new BusinessException("processPaymentRequest: PAYMENT_SERVICE_NOT_AVAILABLE_RIGHT_NOW");
    }*/

    @Override
    public PaymentClient create(Throwable cause) {
        return new PaymentClient() {
            @Override
            public ClientResponse processPaymentRequest(ProcessPaymentRequest request) {
                throw new BusinessException("processPaymentRequest: PAYMENT_SERVICE_NOT_AVAILABLE_RIGHT_NOW. Cause: " + cause.getMessage());
            }

            @Override
            public GetPaymentCardHolderResponse getCardHolder(UUID id) {
                throw new BusinessException("getCardHolder: PAYMENT_SERVICE_NOT_AVAILABLE_RIGHT_NOW. Cause: " + cause.getMessage());
            }
        };
    }
}
