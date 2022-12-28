package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public class PurchaseInvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;

    public PurchaseInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
    }

    @GetMapping("/update/{invoiceId}")
    public String updateInvoice(@PathVariable("invoiceId")Long id, Model model) {
        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("InvoiceProducts", invoiceProductService.findByInvoiceId(id));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        //client vendor model.addAttribute("clientVendor", )
        //product model.addAttribute("
        return  "/invoice/purchase-invoice-update";
    }

    @PostMapping("/update/{invoiceId}")
    public String updateInvoice(@PathVariable("invoiceId")Long id, @ModelAttribute("invoiceDto") InvoiceDto invoiceDto, Model model) {
        invoiceService.updateInvoice(invoiceDto);
        return  "redirect: /PurchaseInvoices/list";
    }

    @GetMapping("/delete/{invoiceId}")
    public String deleteInvoice(@PathVariable("invoiceId")Long id, Model model) {
        invoiceService.deleteInvoice(id);
        return  "redirect: /PurchaseInvoices/list";
    }
    @GetMapping("/list")
    public String listInvoice(Model model){
        model.addAttribute("Invoice", invoiceService.listAllInvoices(InvoiceType.PURCHASE));
        return "/invoice/purchase-invoice-list";
    }

    @GetMapping("/create")
    public String createInvoice(Model model){
        model.addAttribute("invoices", invoiceService.createNewInvoiceDto());
        //  model.addAttribute("clientVendor", )
        return "/invoice/purchase-invoice-create";
    }
    @PostMapping("/create")
    public String createInvoice( @ModelAttribute("invoiceDto") InvoiceDto invoiceDto,  Model model) {
        invoiceDto.setInvoiceType(InvoiceType.PURCHASE);
        invoiceService.create(invoiceDto);
        return "redirect: /PurchaseInvoices/create/"+invoiceDto.getId();
    }

    @GetMapping("/create/{id}")
    public String createInvoice(@PathVariable("id") Long id, Model model){
        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("invoiceProducts", invoiceProductService.findByInvoiceId(id));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        //  model.addAttribute("clientVendor", )
        //  model.addAttribute("products", );
        return  "/invoice/purchase-invoice-create";
    }

}
