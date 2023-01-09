package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/salesInvoices")
public class SalesInvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final ClientVendorService clientVendorService;
    private final ProductService productService;

    public SalesInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService, ClientVendorService clientVendorService, ProductService productService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.clientVendorService = clientVendorService;
        this.productService = productService;
    }


    @GetMapping("/update/{invoiceId}")
    public String navigateToSalesInvoiceUpdate(@PathVariable("invoiceId") Long invoiceId, Model model) throws Exception {
        model.addAttribute("invoice", invoiceService.findInvoiceById(invoiceId));
        model.addAttribute("invoiceProducts", invoiceProductService.getInvoiceProductsOfInvoice(invoiceId));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("clients", clientVendorService.getAllClientVendorsOfCompany(ClientVendorType.CLIENT));
        model.addAttribute("products", productService.getAllProducts());
        return "/invoice/sales-invoice-update";
    }

    @PostMapping("/update/{invoiceId}")
    public String updateSalesInvoice(@PathVariable("invoiceId") Long invoiceId, InvoiceDto invoiceDto) {
        invoiceService.update(invoiceId, invoiceDto);
        return "redirect:/salesInvoices/update/" + invoiceId;
    }

    @GetMapping("/delete/{invoiceId}")
    public String deleteSalesInvoice(@PathVariable("invoiceId") Long invoiceId) {
        invoiceService.delete(invoiceId);
        return "redirect:/salesInvoices/list";
    }

    @GetMapping("/list")
    public String navigateToSalesInvoiceList(Model model) throws Exception {
        model.addAttribute("invoices", invoiceService.getAllInvoicesOfCompany(InvoiceType.SALES));
        return "/invoice/sales-invoice-list";
    }

    @GetMapping("/create")
    public String navigateToSalesInvoiceCreate(Model model) throws Exception {
        model.addAttribute("newSalesInvoice", invoiceService.getNewInvoice(InvoiceType.SALES));
        model.addAttribute("clients", clientVendorService.getAllClientVendorsOfCompany(ClientVendorType.CLIENT));
        return "/invoice/sales-invoice-create";
    }

    @PostMapping("/create")
    public String createNewSalesInvoice(@Valid @ModelAttribute("newSalesInvoice") InvoiceDto newSalesInvoice, BindingResult result, Model model) throws Exception {

        if (result.hasErrors()) {
            model.addAttribute("newSalesInvoice", newSalesInvoice);
            model.addAttribute("clients", clientVendorService.getAllClientVendorsOfCompany(ClientVendorType.CLIENT));
            return "/invoice/sales-invoice-create";
        }
        var invoice = invoiceService.save(newSalesInvoice, InvoiceType.SALES);
        return "redirect:/salesInvoices/update/" + newSalesInvoice.getId();
    }

    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String addInvoiceProductToSalesInvoice(@PathVariable("invoiceId") Long invoiceId, RedirectAttributes redirectAttributes, @Valid @ModelAttribute("newInvoiceProduct") InvoiceProductDto newInvoiceProduct, BindingResult result, Model model) throws Exception {

        if (newInvoiceProduct.getProduct() != null) {

            boolean enoughStock = invoiceProductService.checkProductQuantityBeforeAddingToInvoice(newInvoiceProduct, invoiceId);

            if (!enoughStock) {
                redirectAttributes.addFlashAttribute("error", "Not enough : "+ newInvoiceProduct.getProduct().getName()+" quantity to sell. Only "+newInvoiceProduct.getProduct().getQuantityInStock()+" in stock!");
                return "redirect:/salesInvoices/update/" + invoiceId;
            }
        }
        if (result.hasErrors()) {
            model.addAttribute("invoice", invoiceService.findInvoiceById(invoiceId));
            model.addAttribute("invoiceProducts", invoiceProductService.getInvoiceProductsOfInvoice(invoiceId));
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("clients", clientVendorService.getAllClientVendorsOfCompany(ClientVendorType.CLIENT));
            return "/invoice/sales-invoice-update";
        }
        invoiceProductService.save(invoiceId, newInvoiceProduct);
        return "redirect:/salesInvoices/update/" + invoiceId;
    }

    @GetMapping("removeInvoiceProduct/{invoiceId}/{invoiceProductId}")
    public String removeInvoiceProductFromSalesInvoice(@PathVariable("invoiceId") Long invoiceId, @PathVariable("invoiceProductId") Long invoiceProductId) {
        invoiceProductService.delete(invoiceProductId);
        return "redirect:/salesInvoices/update/" + invoiceId;
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
    public String approve(@PathVariable("invoiceId") long invoiceId,RedirectAttributes redirectAttributes) throws Exception {
      Boolean enoughStock= invoiceProductService.stockCheckBeforeApproval(invoiceId);
        if (!enoughStock) {
            redirectAttributes.addFlashAttribute("error", "Not enough quantity in stock to complete this sale.");
            return "redirect:/salesInvoices/list";
        }
        invoiceService.approve(invoiceId);
        return "redirect:/salesInvoices/list";
    }
}
