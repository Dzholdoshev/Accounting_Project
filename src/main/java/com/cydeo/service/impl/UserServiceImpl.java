package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.RoleService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

 @Service
 public class UserServiceImpl implements UserService {

     private final UserRepository userRepository;
     private final SecurityService securityService;
     private final MapperUtil mapperUtil;
     private final PasswordEncoder passwordEncoder;

     public UserServiceImpl(UserRepository userRepository, RoleService roleService,
                            @Lazy SecurityService securityService, MapperUtil mapperUtil, PasswordEncoder passwordEncoder) {
         this.userRepository = userRepository;
         this.securityService = securityService;
         this.mapperUtil = mapperUtil;
         this.passwordEncoder = passwordEncoder;
     }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return mapperUtil.convert(user, new UserDto());
    }



//    @Override
//    public List<UserDto> getAllFilterForLoggedInUser(UserDto loggedInUser) {
//        switch (loggedInUser.getRole().getDescription()) {
//
//            case "Root User":
//                return getAllFilterForLoggedInUser(loggedInUser).stream()
//                        .filter(user -> user.getRole().getDescription().equals("Admin"))
//                        .collect(Collectors.toList());
//
//            case "Admin":
//                return getAllFilterForLoggedInUser(loggedInUser).stream()
//                        .filter(user -> user.getCompany().equals(loggedInUser.getCompany()))
//                        .collect(Collectors.toList());
//            default:
//                return findAllUsersByCompanyAndRole();
//
//        }
  //  }

    @Override
    public UserDto findUserById(Long id) {
        User user = userRepository.findUserById(id);
        UserDto dto =  mapperUtil.convert(user, new UserDto());
        dto.setIsOnlyAdmin(checkIfOnlyAdminForCompany(dto));
        return dto;

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

    private Boolean checkIfOnlyAdminForCompany(UserDto dto) {
       if (dto.getRole().getDescription().equalsIgnoreCase("Admin")){
           List<User> users = userRepository.findAllByCompany_TitleAndRole_Description(dto.getCompany().getTitle(),"Admin");
           return users.size()==1;
       }
       return false;
    }

    private String getCurrentUserCompanyTitle() {
         String currentUserName = securityService.getLoggedInUser().getUsername();
     return  userRepository.findByUsername(currentUserName).getCompany().getTitle();
    }

    private Boolean isCurrentUserRootUser() {

        return securityService.getLoggedInUser().getRole().getDescription().equalsIgnoreCase("root user");
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

//     @Override
//     public List<UserDto> listAllUsers() {
//
//        List<User> userList = userRepository.findAll();
//             return userList.stream().map(user -> mapperUtil.convert(user, new UserDto()))
//                     .collect(Collectors.toList());
//         }


//     private List<UserDto> findAllUsersByCompanyAndRole() {
//
//          List<UserDto> userList = userRepository.findAllUsersByCompanyAndRole(false).stream()
//            .map(currentUser -> {
//                Boolean isOnlyAdmin =
//                        currentUser.getRole().getDescription().equals("Admin");
//                UserDto userDto = mapperUtil.convert(currentUser, new UserDto());
//                userDto.setIsOnlyAdmin(isOnlyAdmin);
//                return userDto;
//            })
//            .collect(Collectors.toList());
//    return userList;
//    }

}

