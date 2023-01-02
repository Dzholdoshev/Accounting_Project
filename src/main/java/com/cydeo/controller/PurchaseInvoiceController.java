package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/purchaseInvoices")
public class PurchaseInvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;

    private final ProductService productService;

    public PurchaseInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService, ProductService productService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.productService = productService;
    }

    @GetMapping("/update/{invoiceId}")
    public String navigateToPurchaseInvoiceUpdate(@PathVariable("invoiceId") Long invoiceId, Model model) {
        model.addAttribute("invoice", invoiceService.findInvoiceById(invoiceId));
        model.addAttribute("invoiceProducts", invoiceProductService.getInvoiceProductsOfInvoice(invoiceId));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        //client vendor model.addAttribute("vendors", )
        model.addAttribute("products",productService.getAllProducts());
        return "/invoice/purchase-invoice-update";
    }

    @PostMapping("/update/{invoiceId}")
    public String updatePurchaseInvoice(@PathVariable("invoiceId") Long invoiceId, InvoiceDto invoiceDto) {
        invoiceService.update(invoiceId, invoiceDto);
        return "redirect: /purchaseInvoices/list";
    }

    @GetMapping("/delete/{invoiceId}")
    public String deletePurchaseInvoice(@PathVariable("invoiceId") Long invoiceId) {
        invoiceService.delete(invoiceId);
        return "redirect: /purchaseInvoices/list";
    }

    @GetMapping("/list")
    public String navigateToPurchaseInvoiceList(Model model) throws Exception {
        model.addAttribute("invoices", invoiceService.getAllInvoicesOfCompany(InvoiceType.PURCHASE));
        return "/invoice/purchase-invoice-list";
    }

    @GetMapping("/create")
    public String navigateToPurchaseInvoiceCreate(Model model) throws Exception {
        model.addAttribute("newPurchaseInvoice", invoiceService.getNewInvoice(InvoiceType.PURCHASE));
        //   model.addAttribute("vendors") get list of client vendors
        return "/invoice/purchase-invoice-create";
    }

    @PostMapping("/create")
    public String createNewPurchaseInvoice(@Valid @ModelAttribute("invoiceDto") InvoiceDto invoiceDto, BindingResult result, Model model) {

        if (result.hasErrors()) {
            //  model.addAttribute("vendors") list of vendors
            return "/invoice/purchase-invoice-create";
        }
        var invoice = invoiceService.save(invoiceDto, InvoiceType.PURCHASE);
        return "redirect: /purchaseInvoices/update/" + invoice.getId();
    }

    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String addInvoiceProductToPurchaseInvoice(@Valid @PathVariable("invoiceId") Long invoiceId, @ModelAttribute("invoiceProduct") InvoiceProductDto invoiceProductDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("invoice", invoiceService.findInvoiceById(invoiceId));
            model.addAttribute("invoiceProducts", invoiceProductService.getInvoiceProductsOfInvoice(invoiceId));
          //  model.addAttribute("vendors") list of vendors
            model.addAttribute("products",productService.getAllProducts());
            return "/invoice/purchase-invoice-update";
        }
        invoiceProductService.save(invoiceId, invoiceProductDto);
        return "redirect: /purchaseInvoices/update/" + invoiceId;
    }

    @GetMapping("removeInvoiceProduct/{invoiceId}/{invoiceProductId}")
    public String removeInvoiceProductFromPurchaseInvoice(@PathVariable("invoiceId") Long invoiceId, @PathVariable("invoiceProductId") Long invoiceProductId) {
        invoiceProductService.delete(invoiceProductId);
        return "redirect: /purchaseInvoices/update/" + invoiceId;
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
        return "redirect: /purchaseInvoices/list";
    }
}
