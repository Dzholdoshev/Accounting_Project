package com.cydeo.converter;

import com.cydeo.dto.InvoiceDto;
import org.springframework.core.convert.converter.Converter;

public class InvoiceDtoConverter implements Converter<String, InvoiceDto> {
  //  InvoiceService invoiceService;

   // public InvoiceDTOConverter(InvoiceService invoiceService) {
  //      this.invoiceService = invoiceService;
  //  }

    @Override
    public InvoiceDto convert(String source) {
        if (source == null || source.equals("")) {
            return null;
        }
      // return invoiceService.findById(Long.parseLong(source));
        return null;
    }
}
