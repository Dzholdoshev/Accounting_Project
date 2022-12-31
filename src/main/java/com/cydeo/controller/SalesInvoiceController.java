package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Controller
@RequestMapping("/salesInvoices")
public class SalesInvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;

    public SalesInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
    }


    @GetMapping("/update/{invoiceId}")
    public String navigateToSalesInvoiceUpdate(@PathVariable("invoiceId") Long invoiceId, Model model) {
        model.addAttribute("invoice", invoiceService.findInvoiceById(invoiceId));
        model.addAttribute("invoiceProducts", invoiceProductService.getInvoiceProductsOfInvoice(invoiceId));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        //client vendor model.addAttribute("clients", )
        //product model.addAttribute("products", getlistofproducts);
        return "/invoice/sales-invoice-update";
    }

    @PostMapping("/update/{invoiceId}")
    public String updateSalesInvoice(@PathVariable("invoiceId") Long invoiceId, InvoiceDto invoiceDto) {
        invoiceService.update(invoiceId, invoiceDto);
        return "redirect: /salesInvoices/list";
    }

    @GetMapping("/delete/{invoiceId}")
    public String deleteSalesInvoice(@PathVariable("invoiceId") Long invoiceId) {
        invoiceService.delete(invoiceId);
        return "redirect: /salesInvoices/list";
    }

    @GetMapping("/list")
    public String navigateToSalesInvoiceList(Model model) throws Exception {
        model.addAttribute("invoices", invoiceService.getAllInvoicesOfCompany(InvoiceType.SALES));
        return "/invoice/sales-invoice-list";
    }

    @GetMapping("/create")
    public String navigateToSalesInvoiceCreate(Model model) throws Exception {
        model.addAttribute("newSalesInvoice", invoiceService.getNewInvoice(InvoiceType.SALES));
        //  model.addAttribute("clients",) get list of client objects
        return "/invoice/sales-invoice-create";
    }

    @PostMapping("/create")
    public String createNewSalesInvoice(@Valid @ModelAttribute("invoiceDto") InvoiceDto invoiceDto, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("newSalesInvoice", invoiceDto);
            //  model.addAttribute("clients",) get list of client objects
            return "/invoice/sales-invoice-create";
        }
        var invoice = invoiceService.save(invoiceDto, InvoiceType.SALES);
        return "redirect: /salesInvoices/update/" + invoice.getId();
    }

    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String addInvoiceProductToSalesInvoice(@Valid @PathVariable("invoiceId") Long invoiceId, @ModelAttribute("invoiceProduct") InvoiceProductDto invoiceProductDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("newInvoiceProduct", invoiceService.findInvoiceById(invoiceId));
            model.addAttribute("invoiceProducts", invoiceProductService.getInvoiceProductsOfInvoice(invoiceId));
            //product model.addAttribute("products", getlistofproducts);
            //client vendor model.addAttribute("clients", )
            return "/invoice/purchase-invoice-update";
        }
        invoiceProductService.save(invoiceId, invoiceProductDto);
        return "redirect: /salesInvoices/update/" + invoiceId;
    }

    @GetMapping("removeInvoiceProduct/{invoiceId}/{invoiceProductId}")
    public String removeInvoiceProductFromSalesInvoice(@PathVariable("invoiceId") Long invoiceId, @PathVariable("invoiceProductId") Long invoiceProductId) {
        invoiceProductService.delete(invoiceProductId);
        return "redirect: /salesInvoices/update/" + invoiceId;
    }

    @GetMapping("/print/{invoiceId}")
    public String print(@PathVariable("invoiceId") long invoiceId, Model model) {
        InvoiceDto invoice = invoiceService.printInvoice(invoiceId);
        model.addAttribute("company", invoice.getCompany());
        model.addAttribute("invoice", invoice);
        model.addAttribute("invoiceProducts", invoice.getInvoiceProducts());
        return "invoice/invoice_print";
    }

    @GetMapping("/approve/{invoiceId}")
    public String approve(@PathVariable("invoiceId") long invoiceId) {
        invoiceService.approve(invoiceId);
        return "redirect: /salesInvoices/list";
    }
}
