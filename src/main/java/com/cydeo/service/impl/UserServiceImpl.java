package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.RoleService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;


    public UserServiceImpl(SecurityService securityService, UserRepository userRepository,
                           MapperUtil mapperUtil, CompanyService companyService) {
        this.securityService = securityService;
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return mapperUtil.convert(user, new UserDto());
    }

    @Override
    public List<UserDto> getAllFilterForLoggedInUser(UserDto loggedInUser) {
        switch (loggedInUser.getRole().getDescription()) {

            case "Root User":
                return getAllFilterForLoggedInUser(loggedInUser).stream()
                        .filter(user -> user.getRole().getDescription().equals("Admin"))
                        .collect(Collectors.toList());

            case "Admin":
                return getAllFilterForLoggedInUser(loggedInUser).stream()
                        .filter(user -> user.getCompany().equals(loggedInUser.getCompany()))
                        .collect(Collectors.toList());
            default:
                return findAllUsersByCompanyAndRole();

        }
    }

    @Override
    public UserDto findUserById(Long id) {
        User user = userRepository.findUserById(id);
        return mapperUtil.convert(user, new UserDto());
    }

    @Override
    public List<UserDto> getFilteredUsers(){

        List<User> userList;
        if (isCurrentUserRootUser()) {
            userList = userRepository.findAllByRole_Description("Admin");
        } else {
            userList = userRepository.findAllByCompany_Title(getCurrentUserCompanyTitle());
        }
        return userList.stream()
                .sorted(Comparator.comparing((User u) -> u.getCompany().getTitle()).thenComparing(u -> u.getRole().getDescription()))
                .map(entity -> {
                    UserDto dto = mapperUtil.convert(entity, new UserDto());
                    dto.setIsOnlyAdmin(checkIfOnlyAdminForCompany(dto));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private boolean checkIfOnlyAdminForCompany(UserDto userDto) {
      Company company= mapperUtil.convert(userDto.getCompany(),new Company());
        List<User> admins = userRepository.findAllByCompany_Title(company.getTitle().equals("Admin"));
        return userDto.getRole().getDescription().equals("Admin") && admins.size() == 1;
    }

    private Object getCurrentUserCompanyTitle() {
     return  securityService.getLoggedInUser().getCompany().getTitle();
    }

    private boolean isCurrentUserRootUser() {
       User user= mapperUtil.convert(securityService.getLoggedInUser(),new User());
     if (user.getRole().getDescription().equals("Root User")){
         return true;
     }
     return false;
    }


    @Override
    public UserDto save(UserDto user) {

        //User user = mapperUtil.convert(userDto,new User());
        user.setPassword(user.getPassword());
        user.setConfirmPassword(user.getPassword());
        User user1 = mapperUtil.convert(user, new User());
        userRepository.save(user1);
        return user;
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = userRepository.findUserById(userDto.getId());
        User convertedUser = mapperUtil.convert(userDto, new User());
        convertedUser.setId(user.getId());
        convertedUser.setPassword(user.getPassword());
        userRepository.save(convertedUser);
        return findUserById(userDto.getId());
    }

    @Override
    public void delete(Long id) {

        User user = userRepository.findById(id).get();
        if (user==null){
            throw new NoSuchElementException("User was not found");
        }
        user.setIsDeleted(true);
        user.setUsername(user.getUsername()+"-"+user.getId());
        userRepository.save(user);

    }

    @Override
    public Boolean emailExist(UserDto userDto) {
        Optional <User> user = Optional.ofNullable(userRepository.findByUsername(userDto.getUsername()));
        return user.filter(value -> !value.getId().equals(userDto.getId())).isPresent();


    }

       private List<UserDto> findAllUsersByCompanyAndRole() {

          List<UserDto> userList = userRepository.findAllUsersByCompanyAndRole(false).stream()
            .map(currentUser -> {
                Boolean isOnlyAdmin =
                        currentUser.getRole().getDescription().equals("Admin");
                UserDto userDto = mapperUtil.convert(currentUser, new UserDto());
                userDto.setIsOnlyAdmin(isOnlyAdmin);
                return userDto;
            })
            .collect(Collectors.toList());
    return userList;
    }

}

