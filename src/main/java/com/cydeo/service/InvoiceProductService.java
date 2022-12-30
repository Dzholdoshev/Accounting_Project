package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.entity.Product;
import com.cydeo.enums.InvoiceType;

import java.math.BigDecimal;
import java.util.List;


public interface InvoiceProductService {


    InvoiceProductDto findInvoiceProductById(long id);
    List<InvoiceProductDto> getInvoiceProductsOfInvoice(Long invoiceId);
    void save(Long invoiceId, InvoiceProductDto invoiceProductDto);

    void delete(Long invoiceProductId);
    void completeApprovalProcedures(Long invoiceId, InvoiceType type);
    boolean checkProductQuantity(InvoiceProductDto salesInvoiceProduct);
    List<InvoiceProduct> findInvoiceProductsByInvoiceTypeAndProductRemainingQuantity(InvoiceType type, Product product, Integer remainingQuantity);

    List<InvoiceProductDto> findAllInvoiceProductsByProductId(Long id);
}