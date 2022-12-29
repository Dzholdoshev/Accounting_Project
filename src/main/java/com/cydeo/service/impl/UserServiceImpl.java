package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapperUtil, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDto> listAllUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream().map(user -> mapperUtil.convert(user, new UserDto()))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findByUsername(String username) {
        return mapperUtil.convert(userRepository.findByUsername(username), new UserDto());
    }

    @Override
    public void save(UserDto user) {
        user.setPassWord(user.getPassWord());
        user.setConfirmPassword(user.getPassWord());
        User user1 = mapperUtil.convert(user, new User());
        userRepository.save(user1);
    }

    public UserDto update(UserDto userDto) {
        Optional<User> user = userRepository.findById(userDto.getId());
        User convertedUser = mapperUtil.convert(user, new User());
        convertedUser.setId(user.get().getId());
        convertedUser.setPassWord(user.get().getPassWord());
        userRepository.save(convertedUser);
        return findUserById(user.getId());
    }

        @Override
        public List<UserDto> listAllByRole (String role){
            List<User> users = userRepository.findByRoleDescriptionIgnoreCaseAndIsDeleted(role, false);

            return users.stream().map(userMapper::convertToDto).collect(Collectors.toList());
        }

    @Override
    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()->new NoSuchElementException("User not found"));
        return mapperUtil.convert(user, new UserDto());
    }


    @Override
        public void deleteUser (String username){

            User user = userRepository.findByUsername(username).get();

            user.setIsDeleted(true);
            user.setUsername(user.getUsername() + "-" + user.getId());

            userRepository.save(user);

        }
    }

