package com.cydeo.controller;


import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.service.ClientVendorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/clientVendors")
public class ClientVendorController {

    private final ClientVendorService clientVendorService;

    public ClientVendorController(ClientVendorService clientVendorService) {
        this.clientVendorService = clientVendorService;
    }

    @GetMapping("/list")
    public String listClientVendors(Model model) {

        List<ClientVendorDto> clientVendors = clientVendorService.listAllClientVendors();

        model.addAttribute("clientVendors", clientVendors);

        return "clientVendor/clientVendor-list";

    }

    @GetMapping("/update/{id}")
    public String editClientVendor(@PathVariable("id") Long id, Model model) {

        model.addAttribute("clientVendor", clientVendorService.findByClientVendorId(id));

        return "clientVendor/clientVendor-update";
    }

    @GetMapping("/update/{id}")
    public String updateClientVendor(@ModelAttribute ("clientVendor")ClientVendorDto clientVendorDto, Model model) {
        clientVendorService.save(clientVendorDto);
        return "redirect:/clientVendors/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteClientVendor(@PathVariable("id") Long id) {

        clientVendorService.delete(id);

        return "redirect: clientVendors/list";
    }

    @GetMapping("/create")
    public String createClientVendor(Model model) {
        model.addAttribute("clientVendor", new ClientVendorDto());
        model.addAttribute("clientVendorTypes", clientVendorService.)
        return "clientVendor/clientVendor-create";
    }
    @PostMapping("/create")
    public String insertClientVendor(@ModelAttribute("clientVendor") ClientVendorDto clientVendor,BindingResult bindingResult,Model model) {
        if(bindingResult.hasErrors()){
            model.addAttribute("clientVendor", new ClientVendorDto());
            return "clientVendor/clientVendor-create";

        }
        clientVendorService.save(clientVendor);

        return "redirect: clientVendors/list";
    }



}
