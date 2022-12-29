package com.cydeo.service;

import com.cydeo.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> listAllUsers();
    UserDto findByUsername(String username);
    void save(UserDto user);
    //    void deleteByUserName(String username);
    UserDto update(UserDto user);
    void deleteUser(String username);
    List<UserDto> listAllByRole(String role);

    UserDto findUserById(Long id);

}
