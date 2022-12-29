package com.cydeo.service.impl;

import com.cydeo.dto.RoleDto;
import com.cydeo.entity.Role;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {


    private final RoleRepository roleRepository;
    private final MapperUtil mapperUtil;

    public RoleServiceImpl(RoleRepository roleRepository, MapperUtil mapperUtil) {
        this.roleRepository = roleRepository;
        this.mapperUtil = mapperUtil;
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
}
