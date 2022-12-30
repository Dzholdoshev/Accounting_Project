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

    //private final CompanyService companyService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping("/list")
    public String listUsers(Model model) throws Exception {

        model. addAttribute("users", userService.getFilteredUsers());
        return "user/user-list";
    }
    @GetMapping("/update/{username}")
    public String editUser (@PathVariable("username") String username, Model model){

        model.addAttribute("user", userService.findByUsername(username));
        model.addAttribute("roles", roleService.listAllRoles());
        return "user/user-update";
    }


    @PostMapping("/create")
    public String createNewUser(@ModelAttribute("newUser")UserDto userDto,BindingResult result, Model model){
        boolean emailExist = userService.emailExist(userDto); // write this method
        if(result.hasErrors()||emailExist){
            if (emailExist) {
                result.rejectValue("username"," ","User already exists. Please try  with different username");
            }
            return "user/user-create";
        }
        userService.save(userDto);

        return "redirect:/users/list";
    }

//    @PostMapping("/create")
//    public String saveUser(@ModelAttribute("user") UserDto user, BindingResult bindingResult, Model model) throws Exception {
//
//        if (bindingResult.hasErrors()){
//            model.addAttribute("users", userService.getFilteredUsers());
//            model.addAttribute("roles", roleService.listAllRoles());
//            return "user/user-create";
//        }
//        userService.save(user);
//        return "redirect:/user-create";
//    }


    @PostMapping("/update")
    public String update(@ModelAttribute("user") UserDto userDto, BindingResult bindingResult, Model model) throws Exception {

        if (bindingResult.hasErrors()){
            model.addAttribute("users", userService.getFilteredUsers());
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
