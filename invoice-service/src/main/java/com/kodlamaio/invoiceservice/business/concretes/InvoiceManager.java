package com.kodlamaio.invoiceservice.business.concretes;

import com.kodlamaio.commonpackage.configuration.mappers.ModelMapperService;
import com.kodlamaio.commonpackage.events.rental.RentalPaymentCompletedEvent;
import com.kodlamaio.commonpackage.utils.dto.responses.GetCarDetailsResponse;
import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import com.kodlamaio.invoiceservice.api.clients.inventory.CarClient;
import com.kodlamaio.invoiceservice.business.abstracts.InvoiceService;
import com.kodlamaio.invoiceservice.business.dto.responses.get.GetAllInvoicesResponse;
import com.kodlamaio.invoiceservice.business.dto.responses.get.GetInvoiceResponse;
import com.kodlamaio.invoiceservice.business.rules.InvoiceBusinessRules;
import com.kodlamaio.invoiceservice.entities.Invoice;
import com.kodlamaio.invoiceservice.repository.InvoiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class InvoiceManager implements InvoiceService {
    private final InvoiceRepository repository;
    private final ModelMapperService mapper;
    private final InvoiceBusinessRules rules;
    private final CarClient carClient;

    @Override
    public List<GetAllInvoicesResponse> getAll() {
        var iter = repository.findAll();
        var split_iter = iter.spliterator();

        var list = StreamSupport.stream(split_iter, false)
                .map(invoice -> mapper.forResponse().map(invoice, GetAllInvoicesResponse.class))
                .toList();
        return list;
    }

    @Override
    public GetInvoiceResponse getById(String id) {
        rules.checkIfExists(id);
        Invoice invoice = repository.findById(id).orElseThrow();
        return mapper.forResponse().map(invoice, GetInvoiceResponse.class);
    }

    @Override
    public void add(Invoice invoice) {
        invoice.setId(null);
        repository.save(invoice);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public void addByRentalPaymentCompletedEvent(RentalPaymentCompletedEvent event) {
        GetCarDetailsResponse response = getCarDetailsFromClient(event.getCarId());
        Invoice invoice = mapper.forResponse().map(event, Invoice.class);
        mapper.forResponse().map(response, invoice);
        invoice.setRentedAt(Date.from(event.getRentedAt().atZone(ZoneId.systemDefault()).toInstant()));

        /*Invoice invoice = new Invoice(
                null,
                event.getCardHolder(),
                response.getModelName(),
                response.getModelBrandName(),
                response.getPlate(),
                response.getModelYear(),
                response.getDailyPrice(),
                event.getTotalPrice(),
                event.getRentedForDays(),
                Date.from(event.getRentedAt().atZone(ZoneId.systemDefault()).toInstant())
        );*/
        repository.save(invoice);
    }

    private GetCarDetailsResponse getCarDetailsFromClient(UUID carId) {
        try {
            return carClient.getCarDetails(carId);
        } catch (BusinessException businessException) {
            throw new BusinessException("Error on InvoiceManager.add: " + businessException.getMessage());
        }
    }
}
