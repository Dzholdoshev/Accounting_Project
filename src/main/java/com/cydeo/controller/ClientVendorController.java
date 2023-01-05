package com.cydeo.controller;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.UserDto;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/clientVendors")
public class ClientVendorController {

    private final ClientVendorService clientVendorService;
    private final SecurityService securityService;
    private final InvoiceService invoiceService;
    private final ClientVendorRepository clientVendorRepository;

    public ClientVendorController(ClientVendorService clientVendorService, SecurityService securityService, InvoiceService invoiceService, ClientVendorRepository clientVendorRepository) {
        this.clientVendorService = clientVendorService;
        this.securityService = securityService;
        this.invoiceService = invoiceService;
        this.clientVendorRepository = clientVendorRepository;
    }

    @GetMapping("/list")
    public String listClientVendors(Model model) throws Exception {
        UserDto userDto = securityService.getLoggedInUser();
        List<ClientVendorDto> clientVendors = clientVendorService.getAllClientVendorsOfCompany(userDto.getCompany());
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
    public String updateClientVendor(@Valid @ModelAttribute ("clientVendor")ClientVendorDto clientVendorDto,BindingResult bindingResult, Model model) throws Exception {

        if(clientVendorRepository.existsByClientVendorName(clientVendorDto.getClientVendorName())){
            bindingResult.addError(new FieldError("clientVendor","clientVendorName","A client/vendor with this name already exists. Please try with different name."));
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("clientVendorTypes", clientVendorService.getClientVendorType());
            return "clientVendor/clientVendor-update";
        }

        clientVendorService.save(clientVendorDto);
        return "redirect:/clientVendors/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteClientVendor(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws Exception {
        boolean invoiceExists = invoiceService.checkIfInvoiceExist(id);
        if(invoiceExists){
            redirectAttributes.addFlashAttribute("error", "Can not be deleted... You have invoices with this Client/Vendor");
            return "redirect:/clientVendors/list";
        }
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
    public String insertClientVendor(@Valid @ModelAttribute("newClientVendor") ClientVendorDto clientVendor,BindingResult bindingResult,Model model) throws Exception {

        if(clientVendorRepository.existsByClientVendorName(clientVendor.getClientVendorName())){
            bindingResult.addError(new FieldError("newClientVendor","clientVendorName","A client/vendor with this name already exists. Please try with different name."));
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("clientVendorTypes", clientVendorService.getClientVendorType());
            return "/clientVendor/clientVendor-create";
        }
        clientVendorService.save(clientVendor);

        return "redirect:/clientVendors/list";
    }



}
