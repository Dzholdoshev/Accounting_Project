package com.cydeo.service;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.InvoiceProduct;

import java.math.BigDecimal;
import java.util.List;


public interface InvoiceProductService {

    List<InvoiceProductDto> listAllInvoiceProduct();

    List<InvoiceProductDto> findByInvoiceId(Long id);

    void delete(Long invoiceId, Long invoiceProductId);
    InvoiceProductDto createInvoiceProducts(Long id, InvoiceProductDto invoiceProductDto);
}
