package com.cydeo.service;

import com.cydeo.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    UserDto findByUsername(String username);

    List<UserDto> getAllFilterForLoggedInUser(UserDto loggedInUser);

    UserDto findUserById(Long id);

    List<UserDto> getFilteredUsers() throws Exception;
    UserDto save(UserDto userDto);
    UserDto update(UserDto userDto);
    void delete(Long id);
    Boolean emailExist(UserDto userDto);
}
