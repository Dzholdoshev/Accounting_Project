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
    @GetMapping("/update/{id}")
    public String editUser (@PathVariable("id") Long id, Model model){

        model.addAttribute("user", userService.findUserById(id));
        model.addAttribute("userRoles", roleService.listAllRoles());
        return "user/user-update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Long id,UserDto userDto){
       userService.update(userDto);
       return "redirect:/users/list";
    }


    @PostMapping("/create")
    public String createNewUser(@ModelAttribute("newUser")UserDto userDto,BindingResult result, Model model){

        model.addAttribute("user",new UserDto());
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


    @PostMapping("/update{id}")
    public String update(@PathVariable("id")Long id, UserDto userDto, BindingResult bindingResult, Model model) throws Exception {

        if (bindingResult.hasErrors()){
            model.addAttribute("users", userService.getFilteredUsers());
            model.addAttribute("roles", roleService.listAllRoles());
            return "user/user-update";
        }
        userService.update(userDto);
        return "redirect:/user-list";
    }



    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id")Long id){

        userService.delete(id);
        return "redirect:/user-list";
    }

}
