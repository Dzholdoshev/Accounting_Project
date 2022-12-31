package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
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
    private final RoleService roleService;

    private final C

    public UserServiceImpl(SecurityService securityService, UserRepository userRepository,
                           MapperUtil mapperUtil, RoleService roleService) {
        this.securityService = securityService;
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.roleService = roleService;
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found"));
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
    public List<UserDto> getFilteredUsers() throws Exception {

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

    private Object checkIfOnlyAdminForCompany(UserDto userDto) {

    }

    private Object getCurrentUserCompanyTitle() {

    }

    private boolean isCurrentUserRootUser() {



    }

    @Override
    public UserDto save(UserDto userDto) {
        userDto.setPassWord(userDto.getPassWord());
        userDto.setConfirmPassword(userDto.getPassWord());
        User user = mapperUtil.convert(userDto, new User());
        userRepository.save(user);
        return userDto;
    }

    @Override
    public UserDto update(UserDto userDto) {
        Optional<User> user = userRepository.findUserById(userDto.getId());
        User convertedUser = mapperUtil.convert(userDto, new User());
        convertedUser.setId(user.get().getId());
        convertedUser.setPassword(user.get().getPassword());
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
         User user = userRepository.findByUsername(userDto.getUserName());
        if (user != null) {
            return true;
        }
        return false;
    }

       private List<UserDto> findAllUsersByCompanyAndRole() {

          List<UserDto> list = userRepository.findAllUsersByCompanyAndRole(false).stream()
            .map(currentUser -> {
                Boolean isOnlyAdmin =
                        currentUser.getRole().getDescription().equals("Admin");
                UserDto userDto = mapperUtil.convert(currentUser, new UserDto());
                userDto.setIsOnlyAdmin(isOnlyAdmin);
                return userDto;
            })
            .collect(Collectors.toList());
    return list;
    }

}

