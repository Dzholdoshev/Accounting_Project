package com.cydeo.converter;

import com.cydeo.dto.ClientVendorDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientVendorDtoConverter implements Converter<String, ClientVendorDto> {

//private final ClientVendorService clientVendorService;

   // public ClientVendorDtoConverter(ClientVendorService clientVendorService) {
   //     this.clientVendorService = clientVendorService;
   // }

    @Override
    public ClientVendorDto convert(String source) {
        if (source == null || source.equals("")) {
            return null;
        }
        return null;
       // return clientVendorService.findByClientVendorName(source);
    }
}
