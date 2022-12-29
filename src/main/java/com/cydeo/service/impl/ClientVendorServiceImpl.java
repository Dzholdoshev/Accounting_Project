package com.cydeo.service.impl;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientVendorServiceImpl implements ClientVendorService {
    private final ClientVendorRepository clientVendorRepository;
    private final MapperUtil mapperUtil;
    private final UserService userService;

    public ClientVendorServiceImpl(ClientVendorRepository clientVendorRepository, MapperUtil mapperUtil, UserService userService) {
        this.clientVendorRepository = clientVendorRepository;
        this.mapperUtil = mapperUtil;
        this.userService = userService;
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

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto loggedInUser = userService.findByUsername(username);


        if (clientVendor.isPresent()) {
          // convertedClientVendor.setLastUpdateUserId(loggedInUser.getId()); //should be changed to id of authenticated user
            convertedClientVendor.setLastUpdateDateTime(LocalDateTime.now());
            clientVendorRepository.save(convertedClientVendor);
        }

    }

    @Override
    public void save(ClientVendorDto dto) {

        ClientVendor clientVendor = mapperUtil.convert(dto, new ClientVendor());
        clientVendorRepository.save(clientVendor);

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
    public ClientVendorDto findByClientVendorId(Long id) {
        Optional<ClientVendor> clientVendor = clientVendorRepository.findById(id);
        return mapperUtil.convert(clientVendor.get(), new ClientVendorDto());
    }
}
