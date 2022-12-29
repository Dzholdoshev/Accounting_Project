package com.cydeo.converter;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.service.InvoiceProductService;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;

public class InvoiceProductDtoConverter implements Converter<String, InvoiceDto> {
    InvoiceProductService invoiceProductService;

    public InvoiceProductDtoConverter(@Lazy InvoiceProductService invoiceProductService) {
        this.invoiceProductService = invoiceProductService;
    }

    @Override
    public InvoiceDto convert(String id) {
        if (id == null || id.equals("")) {
            return null;
        }
        return invoiceProductService.findInvoiceProductById(Long.parseLong(id));
    }
}
