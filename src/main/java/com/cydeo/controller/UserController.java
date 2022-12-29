package com.cydeo.controller;

import com.cydeo.dto.UserDto;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping("/list")
    public String listAllUsers(Model model){

        model. addAttribute("users", userService.listAllUsers());
        return "user/user-list";
    }


    @GetMapping("/create")
    public String createUser(Model model){

        model.addAttribute("newUser", new UserDto());
        model.addAttribute("userRoles", roleService.listAllRoles());
        model.addAttribute("users", userService.listAllUsers());
        return "user/user-create";
    }

    @PostMapping("/create")
    public String saveUser(@ModelAttribute("user") UserDto user, BindingResult bindingResult, Model model){

        if (bindingResult.hasErrors()){
            model.addAttribute("users", userService.listAllUsers());
            model.addAttribute("roles", roleService.listAllRoles());
            return "user/user-create";
        }
        userService.save(user);
        return "redirect:/user-create";
    }


    @GetMapping("/update/{username}")
    public String editUser (@PathVariable("username") String username, Model model){

        model.addAttribute("user", userService.findByUsername(username));
        model.addAttribute("roles", roleService.listAllRoles());
        return "user/user-update";
    }


    @PostMapping("/update")
    public String update(@ModelAttribute("user") UserDto userDto, BindingResult bindingResult, Model model){

        if (bindingResult.hasErrors()){
            model.addAttribute("users", userService.listAllUsers());
            model.addAttribute("roles", roleService.listAllRoles());
            return "user/user-update";
        }
        userService.update(userDto);
        return "redirect:/user-list";
    }



    @GetMapping("/delete/{username}")
    public String deleteUser(@PathVariable("username") String username){

        userService.deleteUser(username);
        return "redirect:/user-list";
    }

}
