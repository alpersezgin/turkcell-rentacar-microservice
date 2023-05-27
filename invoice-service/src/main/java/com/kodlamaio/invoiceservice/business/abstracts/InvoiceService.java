package com.kodlamaio.invoiceservice.business.abstracts;

import com.kodlamaio.commonpackage.events.rental.RentalPaymentCompletedEvent;
import com.kodlamaio.invoiceservice.business.dto.responses.get.GetAllInvoicesResponse;
import com.kodlamaio.invoiceservice.business.dto.responses.get.GetInvoiceResponse;
import com.kodlamaio.invoiceservice.entities.Invoice;

import java.util.List;

public interface InvoiceService {
    List<GetAllInvoicesResponse> getAll();

    GetInvoiceResponse getById(String id);

    void add(Invoice invoice);

    void delete(String id);

    void addByRentalPaymentCompletedEvent(RentalPaymentCompletedEvent event);
}
