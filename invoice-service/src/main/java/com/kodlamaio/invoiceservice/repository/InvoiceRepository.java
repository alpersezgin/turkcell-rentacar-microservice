package com.kodlamaio.invoiceservice.repository;

import com.kodlamaio.invoiceservice.entities.Invoice;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface InvoiceRepository extends ElasticsearchRepository<Invoice, String> {
}
