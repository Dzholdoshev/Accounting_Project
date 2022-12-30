package com.cydeo.service.impl;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ClientVendorServiceImpl implements ClientVendorService {

    private final ClientVendorRepository clientVendorRepository;
    private final MapperUtil mapperUtil;
    private final UserService userService;
    private final SecurityService securityService;

    public ClientVendorServiceImpl(ClientVendorRepository clientVendorRepository, MapperUtil mapperUtil, UserService userService, SecurityService securityService) {
        this.clientVendorRepository = clientVendorRepository;
        this.mapperUtil = mapperUtil;
        this.userService = userService;
        this.securityService = securityService;
    }

    @Override
    public ClientVendorDto findClientVendorById(Long id) {
        Optional<ClientVendor> clientVendor = clientVendorRepository.findClientVendorById(id);
        return mapperUtil.convert(clientVendor.get(), new ClientVendorDto());
    }

    @Override
    public List<ClientVendorDto> getAllClientVendors() throws Exception {
        return clientVendorRepository.findAll()
                .stream()
                .map(clientVendor -> mapperUtil.convert(clientVendor, new ClientVendorDto()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientVendorDto> getAllClientVendorsOfCompany(ClientVendorType clientVendorType) {
        return null;
    }

    @Override
    public ClientVendorDto create(ClientVendorDto clientVendorDto) throws Exception {
        return null;
    }

    /*
        @Override
        public List<ClientVendorDto> getAllClientVendorsOfCompany(ClientVendorType clientVendorType) {
            Company company =  securityService.getLoggedInUser().getCompany();
         List<ClientVendor> clientVendorList =  clientVendorRepository.findAllByCompanyAndClientVendorType(company,clientVendorType);
         return clientVendorList.stream()
                 .map(clientVendor -> mapperUtil.convert(clientVendor,new ClientVendorDto()))
                 .collect(Collectors.toList());
        }


        Optional<ClientVendor> clientVendor = clientVendorRepository.findById(dto.getId());
        ClientVendor convertedClientVendor = mapperUtil.convert(dto, new ClientVendor());
     */
/*
    @Override
    public ClientVendorDto create(ClientVendorDto clientVendorDto) throws Exception {
       ClientVendorDto newClientVendorDto = new ClientVendorDto();
       ClientVendor newClientVendor = mapperUtil.convert(newClientVendorDto,new ClientVendor());
       newClientVendor.setInsertDateTime(LocalDateTime.now());
       newClientVendor.setInsertUserId(securityService.getLoggedInUser().getId());

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto loggedInUser = userService.findByUsername(username);
        clientVendorRepository.save(newClientVendor);
        return findClientVendorById(newClientVendor.getId());
    }


 */
    @Override
    public ClientVendorDto update(Long id, ClientVendorDto clientVendorDto) throws ClassNotFoundException, CloneNotSupportedException {
        Optional<ClientVendor> clientVendor = clientVendorRepository.findById(id);
        ClientVendor convertedClientVendor = mapperUtil.convert(clientVendorDto, new ClientVendor());
        UserDto loggedInUser = securityService.getLoggedInUser();
        if (clientVendor.isPresent()) {
            // convertedClientVendor.setLastUpdateUserId(loggedInUser.getId()); //should be changed to id of authenticated user
            //convertedClientVendor.setLastUpdateUserId(loggedInUser.getId()); //should be changed to id of authenticated user
            convertedClientVendor.setLastUpdateDateTime(LocalDateTime.now());
            clientVendorRepository.save(convertedClientVendor);
        }
        return findClientVendorById(clientVendorDto.getId());
    }

    @Override
    public ClientVendorDto save(ClientVendorDto dto) {
        ClientVendor clientVendor = mapperUtil.convert(dto, new ClientVendor());
        clientVendorRepository.save(clientVendor);
        return findClientVendorById(clientVendor.getId());
    }

    @Override
    public void delete(Long id) {
        Optional<ClientVendor> foundClientVendor = clientVendorRepository.findById(id);
        if (foundClientVendor.isPresent()) {
            foundClientVendor.get().setIsDeleted(true);
            clientVendorRepository.save(foundClientVendor.get());
        }

    }



    @Override
    public boolean companyNameExists(ClientVendorDto clientVendorDto) {
        return clientVendorRepository.existsById(clientVendorDto.getId());
    }

    @Override
    public List<String> getClientVendorType() {
        return Stream.of(ClientVendorType.values())
                .map(ClientVendorType::getValue)
                .collect(Collectors.toList());
        //List.of(ClientVendorType.CLIENT.getValue(), ClientVendorType.VENDOR.getValue());
    }


}