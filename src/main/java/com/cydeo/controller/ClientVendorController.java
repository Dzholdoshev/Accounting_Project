package com.cydeo.controller;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.service.ClientVendorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/clientVendors")
public class ClientVendorController {

    private final ClientVendorService clientVendorService;
    public ClientVendorController(ClientVendorService clientVendorService) {
        this.clientVendorService = clientVendorService;
    }

    @GetMapping("/list")
    public String listClientVendors(Model model) throws Exception {
        List<ClientVendorDto> clientVendors = clientVendorService.getAllClientVendors();
        model.addAttribute("clientVendors", clientVendors);
        return "clientVendor/clientVendor-list";

    }

    @GetMapping("/update/{id}")
    public String editClientVendor(@PathVariable("id") Long id, Model model) {

        model.addAttribute("clientVendor", clientVendorService.findClientVendorById(id));
        model.addAttribute("clientVendorTypes", clientVendorService.getClientVendorType());
        return "clientVendor/clientVendor-update";
    }

    @PostMapping("/update/{id}")
    public String updateClientVendor(@Valid @ModelAttribute ("clientVendor")ClientVendorDto clientVendorDto, Model model) {
        clientVendorService.save(clientVendorDto);
        return "redirect:/clientVendors/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteClientVendor(@PathVariable("id") Long id) {
        clientVendorService.delete(id);
        return "redirect:/clientVendors/list";
    }

    @GetMapping("/create")
    public String createClientVendor(Model model) {
        model.addAttribute("newClientVendor", new ClientVendorDto());
        model.addAttribute("clientVendorTypes", clientVendorService.getClientVendorType());
        return "/clientVendor/clientVendor-create";
    }
    @PostMapping("/create")
    public String insertClientVendor(@Valid @ModelAttribute("newClientVendor") ClientVendorDto clientVendor,BindingResult bindingResult,Model model) {

        if(bindingResult.hasErrors()){
            model.addAttribute("clientVendorTypes", clientVendorService.getClientVendorType());
            return "/clientVendor/clientVendor-create";

        }
        clientVendorService.save(clientVendor);

        return "redirect: clientVendors/list";
    }



}
