package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ClientVendorService {
    List<ClientVendorDto> listAllClientVendors();
    void complete(String name);
    void update(ClientVendorDto dto);
    void save(ClientVendorDto dto);
    void delete(Long id);


    ClientVendorDto findByClientVendorId(Long id);
}
