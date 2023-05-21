package com.kodlamaio.paymentservice.business.concretes;

import com.kodlamaio.commonpackage.configuration.mappers.ModelMapperService;
import com.kodlamaio.commonpackage.utils.constants.Messages;
import com.kodlamaio.commonpackage.utils.dto.ClientResponse;
import com.kodlamaio.commonpackage.utils.dto.ProcessPaymentRequest;
import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import com.kodlamaio.paymentservice.business.abstracts.PaymentService;
import com.kodlamaio.paymentservice.business.abstracts.PosService;
import com.kodlamaio.paymentservice.business.dto.requests.create.CreatePaymentRequest;
import com.kodlamaio.paymentservice.business.dto.requests.update.UpdatePaymentRequest;
import com.kodlamaio.paymentservice.business.dto.responses.create.CreatePaymentResponse;
import com.kodlamaio.paymentservice.business.dto.responses.get.GetAllPaymentsResponse;
import com.kodlamaio.paymentservice.business.dto.responses.get.GetPaymentResponse;
import com.kodlamaio.paymentservice.business.dto.responses.update.UpdatePaymentResponse;
import com.kodlamaio.paymentservice.business.rules.PaymentBusinessRules;
import com.kodlamaio.paymentservice.entities.Payment;
import com.kodlamaio.paymentservice.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentManager implements PaymentService {
    private final PaymentRepository repository;
    private final ModelMapperService mapper;
    private final PaymentBusinessRules rules;
    private final PosService posService;

    @Override
    public List<GetAllPaymentsResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(payment -> mapper.forResponse().map(payment, GetAllPaymentsResponse.class))
                .toList();
    }

    @Override
    public GetPaymentResponse getById(UUID id) {
        rules.checkIfPaymentExists(id);
        Payment payment = repository.findById(id).orElseThrow();
        return mapper.forResponse().map(payment, GetPaymentResponse.class);
    }

    @Override
    public CreatePaymentResponse add(CreatePaymentRequest request) {
        rules.checkIfCardExists(request.getCardNumber());
        Payment payment = mapper.forRequest().map(request, Payment.class);
        payment.setId(UUID.randomUUID());
        Payment savedPayment = repository.save(payment);
        return mapper.forResponse().map(savedPayment, CreatePaymentResponse.class);
    }

    @Override
    public UpdatePaymentResponse update(UUID id, UpdatePaymentRequest request) {
        rules.checkIfPaymentExists(id);
        Payment payment = mapper.forRequest().map(request, Payment.class);
        payment.setId(id);
        Payment updatedPayment = repository.save(payment);
        return mapper.forResponse().map(updatedPayment, UpdatePaymentResponse.class);
    }

    @Override
    public void delete(UUID id) {
        rules.checkIfPaymentExists(id);
        repository.deleteById(id);
    }

    @Override
    public ClientResponse processPayment(ProcessPaymentRequest request) {
        rules.checkIfPaymentExists(request.getPaymentId());
        Payment payment = repository.findById(request.getPaymentId()).orElseThrow();
        //rules.checkIfPaymentIsValid(payment);
        try {
            rules.checkIfBalanceIdEnough(payment.getBalance(), request.getPrice());
        } catch (BusinessException businessException) {
            return new ClientResponse(false, businessException.getMessage());
        }

        try {
            posService.pay(); // fake pos service
            payment.setBalance(payment.getBalance() - request.getPrice());
            repository.save(payment);
            return new ClientResponse(true, Messages.Payment.Accepted);
        } catch (BusinessException exception) {
            return new ClientResponse(false, exception.getMessage());
        }
    }
}
