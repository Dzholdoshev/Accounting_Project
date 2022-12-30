package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;

    public UserServiceImpl(SecurityService securityService, UserRepository userRepository,
                           MapperUtil mapperUtil) {
        this.securityService = securityService;
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
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
        List<User> userList=userRepository.findAll();
        return userList.stream().map(user -> mapperUtil
                .convert(user,new UserDto())).collect(Collectors.toList());
    }

    @Override
    public UserDto save(UserDto userDto) {
        userDto.setPassWord(userDto.getPassWord());
        userDto.setConfirmPassword(userDto.getPassWord());
        User user = mapperUtil.convert(userDto, new User());
        userRepository.save(user);
    }

    @Override
    public UserDto update(UserDto userDto) {
        Optional<User> user = userRepository.findById(userDto.getId());
        User convertedUser = mapperUtil.convert(userDto, new User());
        convertedUser.setId(user.get().getId());
        convertedUser.setPassWord(user.get().getPassWord());
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

    //    public List<UserDto> listAllUsersByLoggedInStatus() {
//        if (securityService.getLoggedInUser().getRole().getDescription().equals("Admin")) {
//            return listAllUsers().stream()
//                    .filter(userDto -> userDto.getCompany().getId().equals(securityService.getLoggedInUser()
//                            .getCompany().getId())).collect(Collectors.toList());
//        } else if (securityService.getLoggedInUser().getRole().getDescription().equals("Root User")) {
//            return listAllUsers().stream().filter(userDto -> userDto.getRole().getDescription().equals("Admin")).collect(Collectors.toList());
//        } else {
//            throw new NoSuchElementException("No users is available");
//        }
//    }
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

