package com.cydeo.service.impl;

import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Role;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {


    private final RoleRepository roleRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;

    public RoleServiceImpl(RoleRepository roleRepository, MapperUtil mapperUtil, SecurityService securityService) {
        this.roleRepository = roleRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
    }

    @Override
    public List<RoleDto> listAllRoles() {
        List<Role> roleList = roleRepository.findAll();
        return roleList.stream().map(role -> mapperUtil.convert(role, new RoleDto()))
                .collect(Collectors.toList());
    }


    @Override
    public RoleDto findRoleById(Long id) {
        return mapperUtil.convert(roleRepository.findById(id), new RoleDto());
    }

    @Override
    public List<RoleDto> getRolesFilterForLoggedUser() {
        return null;
    }


        @Override
        public List<RoleDto> getFilteredRolesForCurrentUser() {

            UserDto user = securityService.getLoggedInUser();
            if (user.getRole().getDescription().equals("Root User")) {
                List<RoleDto> list = new ArrayList<>();
                list.add(mapperUtil.convert(roleRepository.findByDescription("Admin"), new RoleDto()));
                return list;
            } else {
                return roleRepository.findAll().stream().filter(role -> !role.getDescription().equals("Root User"))
                        .map(role -> mapperUtil.convert(role, new RoleDto()))
                        .collect(Collectors.toList());
            }

        }
}
