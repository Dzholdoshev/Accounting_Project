package com.cydeo.controller;


import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.service.ClientVendorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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



}
