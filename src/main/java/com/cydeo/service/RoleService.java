package com.cydeo.service;

import com.cydeo.dto.RoleDto;

import java.util.List;

public interface RoleService {

    List<RoleDto> listAllRoles();
    RoleDto findRoleById(Long id);
    List<RoleDto> getRolesFilterForLoggedUser();
}
