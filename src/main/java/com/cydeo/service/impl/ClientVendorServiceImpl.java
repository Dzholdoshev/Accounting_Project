package com.cydeo.service.impl;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.service.ClientVendorService;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientVendorServiceImpl implements ClientVendorService {
    private final ClientVendorRepository clientVendorRepository;
    private final MapperUtil mapperUtil;

    public ClientVendorServiceImpl(ClientVendorRepository clientVendorRepository, MapperUtil mapperUtil) {
        this.clientVendorRepository = clientVendorRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public List<ClientVendorDto> listAllClientVendors() {
        return clientVendorRepository.findAll()
                .stream()
                .map(clientVendor -> mapperUtil.convert(clientVendor, new ClientVendorDto()))
                .collect(Collectors.toList());
    }

    @Override
    public void complete(String name) {

    }

    @Override
    public void update(ClientVendorDto dto) {


        Optional<ClientVendor> clientVendor = clientVendorRepository.findById(dto.getId());
        ClientVendor convertedClientVendor = mapperUtil.convert(dto, new ClientVendor());

if(clientVendor.isPresent()){
convertedClientVendor.setLastUpdateUserId(1L); //should be changed to id of authenticated user
convertedClientVendor.setLastUpdateDateTime(LocalDateTime.now());
clientVendorRepository.save(convertedClientVendor);
}

    }

    @Override
    public void save(ClientVendorDto dto) {

    }

    @Override
    public void delete(String name) {

    }

    @Override
    public ClientVendorDto findByClientVendorId(Long id) {
        Optional<ClientVendor> clientVendor = clientVendorRepository.findById(id);
        return mapperUtil.convert(clientVendor.get(), new ClientVendorDto());
    }
}
