package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;

import java.util.List;

public interface InvoiceService {


    List<InvoiceDto> listAllInvoices();

    void updateInvoice(InvoiceDto invoiceDto);

    void deleteInvoice(Long id);

    void approveInvoice(Long id);

    InvoiceDto create(InvoiceDto invoiceDto);
    InvoiceDto findById(Long id);
}
