package com.cydeo.converter;

import com.cydeo.dto.InvoiceDto;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class InvoiceDtoConverter implements Converter<String, InvoiceDto> {
  //  InvoiceService invoiceService;

   // public InvoiceDTOConverter(InvoiceService invoiceService) {
  //      this.invoiceService = invoiceService;
  //  }

    @Override
    public InvoiceDto convert(String id) {
//        if (id == null || id.equals("")) {
//            return null;
//        }
      // return invoiceService.findInvoiceById(Long.parseLong(id));
        return null;
    }
}
