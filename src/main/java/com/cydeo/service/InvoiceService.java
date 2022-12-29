package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.enums.InvoiceType;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceService {


    List<InvoiceDto> listAllInvoices(InvoiceType invoiceType);

    void updateInvoice(InvoiceDto invoiceDto);

    void deleteInvoice(Long id);

    void approveInvoice(Long id);

    InvoiceDto create(InvoiceDto invoiceDto);
    InvoiceDto findInvoiceById(long id);
    InvoiceDto createNewPurchaseInvoiceDto();
    BigDecimal invoiceTotalPrice(InvoiceDto invoiceDto);
    BigDecimal invoicePrice(InvoiceDto invoiceDto);
    Integer invoiceTax(InvoiceDto invoiceDto);
}
