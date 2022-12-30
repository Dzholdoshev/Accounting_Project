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
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;

    //private final CompanyService companyService;

    public UserServiceImpl(UserRepository userRepository,
                           MapperUtil mapperUtil, SecurityService securityService) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow( () -> new NoSuchElementException("User not found"));
        return mapperUtil.convert(user, new UserDto());
    }
    private List<UserDto> findAllOrderByCompanyAndRole() {

        List<UserDto> list = userRepository.findAllOrderByCompanyAndRole(false).stream()
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

    @Override
    public List<UserDto> findAllFilterForLoggedInUser() {
        UserDto loggedInUser = securityService.getLoggedInUser();
        switch (loggedInUser.getRole().getDescription()) {

            case "Root User":
                return findAllOrderByCompanyAndRole().stream()
                        .filter(user -> user.getRole().getDescription().equals("Admin"))
                        .collect(Collectors.toList());
            case "Admin":
                return findAllOrderByCompanyAndRole().stream()
                        .filter(user -> user.getCompany().equals(loggedInUser.getCompany()))
                        .collect(Collectors.toList());
            default:
                return findAllOrderByCompanyAndRole();

        }
    }
}
