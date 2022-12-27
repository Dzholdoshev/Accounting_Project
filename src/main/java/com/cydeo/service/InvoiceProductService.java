package com.cydeo.service;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.InvoiceProduct;

import java.math.BigDecimal;
import java.util.List;


public interface InvoiceProductService {

    List<InvoiceProductDto> listAllInvoiceProduct();

    BigDecimal findPriceByInvoiceNo(String invoiceNo);

    List<InvoiceProductDto> findByInvoiceNo(String invoiceNo);

    List<InvoiceProductDto> updateProducts(List<InvoiceProductDto> updatedProductList);
}
