package com.cydeo.service;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceType;

import java.math.BigDecimal;
import java.util.List;


public interface InvoiceProductService {

    List<InvoiceProductDto> listAllInvoiceProduct();

    List<InvoiceProductDto> findAllInvoiceProductsByProductId(long id);

    void delete(Long invoiceId, Long invoiceProductId);
    InvoiceProductDto save(Long id, InvoiceProductDto invoiceProductDto);

    List<InvoiceProductDto> findByInvoiceTypesAndProductRemainingQuantity(InvoiceType invoiceType,?);
}
