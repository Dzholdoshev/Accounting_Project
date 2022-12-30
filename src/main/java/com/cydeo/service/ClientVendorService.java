package com.cydeo.service;


import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.enums.ClientVendorType;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ClientVendorService {
    List<ClientVendorDto> listAllClientVendors();
    void complete(String name);
    void update(ClientVendorDto dto);
    ClientVendorDto findClientVendorById(Long id);
    List<ClientVendorDto> getAllClientVendors() throws Exception;
    List<ClientVendorDto> getAllClientVendorsOfCompany(ClientVendorType clientVendorType);
    ClientVendorDto create(ClientVendorDto clientVendorDto) throws Exception;
    ClientVendorDto update(Long id, ClientVendorDto clientVendorDto) throws ClassNotFoundException, CloneNotSupportedException;
    void delete(Long id);
    // void update(ClientVendorDto dto);
    void save(ClientVendorDto dto);
    boolean companyNameExists(ClientVendorDto clientVendorDto);
    List<String> getClientVendorType();


    ClientVendorDto findByClientVendorId(Long id);
}