package com.cydeo.controller;

import com.cydeo.EmailContext;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.*;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.sql.DataSource;
import javax.validation.Valid;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

@Controller
@RequestMapping("/salesInvoices")
public class SalesInvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final ClientVendorService clientVendorService;
    private final ProductService productService;
    private final EmailSenderService emailSenderService;
    private final PageSaver pageSaver;


    public SalesInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService, ClientVendorService clientVendorService, ProductService productService, EmailSenderService emailSenderService, PageSaver pageSaver) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.clientVendorService = clientVendorService;
        this.productService = productService;
        this.emailSenderService = emailSenderService;
        this.pageSaver = pageSaver;
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

    @GetMapping("/print/{invoiceId}/sent")
    public String sentEmail(@PathVariable("invoiceId") long invoiceId, Model model) throws MessagingException, IOException {
        InvoiceDto invoice = invoiceService.printInvoice(invoiceId);
        model.addAttribute("company", invoice.getCompany());
        model.addAttribute("invoice", invoice);
        model.addAttribute("invoiceProducts", invoice.getInvoiceProducts());




        //print(invoiceId,model);

        FileSystemResource fileSystemResource = new FileSystemResource("src/main/resources/templates/invoice/invoice_print.html");

        String str = print(invoiceId, model);
      String page = pageSaver.savePage("http://localhost:8001/salesInvoices/print/3/sent");

//emailSenderService.sendMail(new EmailContext().setAttachment(););
        FileSystemResource file= new FileSystemResource(ResourceUtils.getFile("src/main/resources/templates/invoice/invoice_print.html"));
;
emailSenderService.sendEmailAttach("Test",
        "<!DOCTYPE html>\n" +
        "<html class=\"loading\" lang=\"en\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
        "<!-- BEGIN : Head-->\n" +
        "\n" +
        "<head>\n" +
        "  <title>Payment Success</title>\n" +
        "  <meta charset=\"utf-8\"/>\n" +
        "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\"/>\n" +
        "  <link rel=\"stylesheet\" th:href=\"@{/assets/vendor/bootstrap/css/bootstrap.min.css}\"/>\n" +
        "  <link rel=\"shortcut icon\" type=\"image/x-icon\" th:href=\"@{/img/ico/favicon.ico}\">\n" +
        "  <style>\n" +
        "    body{margin-top:20px;\n" +
        "      background-color: #f7f7ff;\n" +
        "    }\n" +
        "    #invoice {\n" +
        "      padding: 0px;\n" +
        "    }\n" +
        "\n" +
        "    .invoice {\n" +
        "      position: relative;\n" +
        "      background-color: #FFF;\n" +
        "      min-height: 680px;\n" +
        "      padding: 15px\n" +
        "    }\n" +
        "\n" +
        "    .invoice header {\n" +
        "      padding: 10px 0;\n" +
        "      margin-bottom: 20px;\n" +
        "      border-bottom: 1px solid #0d6efd\n" +
        "    }\n" +
        "\n" +
        "    .invoice .company-details {\n" +
        "      text-align: right\n" +
        "    }\n" +
        "\n" +
        "    .invoice .company-details .name {\n" +
        "      margin-top: 0;\n" +
        "      margin-bottom: 0\n" +
        "    }\n" +
        "\n" +
        "    .invoice .contacts {\n" +
        "      margin-bottom: 20px\n" +
        "    }\n" +
        "\n" +
        "    .invoice .invoice-to {\n" +
        "      text-align: left\n" +
        "    }\n" +
        "\n" +
        "    .invoice .invoice-to .to {\n" +
        "      margin-top: 0;\n" +
        "      margin-bottom: 0\n" +
        "    }\n" +
        "\n" +
        "    .invoice .invoice-details {\n" +
        "      text-align: right\n" +
        "    }\n" +
        "\n" +
        "    .invoice .invoice-details .invoice-id {\n" +
        "      margin-top: 0;\n" +
        "      color: #0d6efd\n" +
        "    }\n" +
        "\n" +
        "    .invoice main {\n" +
        "      padding-bottom: 50px\n" +
        "    }\n" +
        "\n" +
        "    .invoice main .thanks {\n" +
        "      margin-top: -100px;\n" +
        "      font-size: 2em;\n" +
        "      margin-bottom: 50px\n" +
        "    }\n" +
        "\n" +
        "    .invoice main .notices {\n" +
        "      padding-left: 6px;\n" +
        "      border-left: 6px solid #0d6efd;\n" +
        "      background: #e7f2ff;\n" +
        "      padding: 10px;\n" +
        "    }\n" +
        "\n" +
        "    .invoice main .notices .notice {\n" +
        "      font-size: 1.2em\n" +
        "    }\n" +
        "\n" +
        "    .invoice table {\n" +
        "      width: 100%;\n" +
        "      border-collapse: collapse;\n" +
        "      border-spacing: 0;\n" +
        "      margin-bottom: 20px\n" +
        "    }\n" +
        "\n" +
        "    .invoice table td,\n" +
        "    .invoice table th {\n" +
        "      padding: 15px;\n" +
        "      background: #eee;\n" +
        "      border-bottom: 1px solid #fff\n" +
        "    }\n" +
        "\n" +
        "    .invoice table th {\n" +
        "      white-space: nowrap;\n" +
        "      font-weight: 400;\n" +
        "      font-size: 16px\n" +
        "    }\n" +
        "\n" +
        "    .invoice table td h3 {\n" +
        "      margin: 0;\n" +
        "      font-weight: 400;\n" +
        "      color: #0d6efd;\n" +
        "      font-size: 1.2em\n" +
        "    }\n" +
        "\n" +
        "    .invoice table .qty,\n" +
        "    .invoice table .total,\n" +
        "    .invoice table .unit {\n" +
        "      text-align: right;\n" +
        "      font-size: 1.2em\n" +
        "    }\n" +
        "\n" +
        "    .invoice table .no {\n" +
        "      color: #fff;\n" +
        "      font-size: 1.6em;\n" +
        "      background: #0d6efd\n" +
        "    }\n" +
        "\n" +
        "    .invoice table .unit {\n" +
        "      background: #ddd\n" +
        "    }\n" +
        "\n" +
        "    .invoice table .total {\n" +
        "      background: #0d6efd;\n" +
        "      color: #fff\n" +
        "    }\n" +
        "\n" +
        "    .invoice table tbody tr:last-child td {\n" +
        "      border: none\n" +
        "    }\n" +
        "\n" +
        "    .invoice table tfoot td {\n" +
        "      background: 0 0;\n" +
        "      border-bottom: none;\n" +
        "      white-space: nowrap;\n" +
        "      text-align: right;\n" +
        "      padding: 10px 20px;\n" +
        "      font-size: 1.2em;\n" +
        "      border-top: 1px solid #aaa\n" +
        "    }\n" +
        "\n" +
        "    .invoice table tfoot tr:first-child td {\n" +
        "      border-top: none\n" +
        "    }\n" +
        "    .card {\n" +
        "      position: relative;\n" +
        "      display: flex;\n" +
        "      flex-direction: column;\n" +
        "      min-width: 0;\n" +
        "      word-wrap: break-word;\n" +
        "      background-color: #fff;\n" +
        "      background-clip: border-box;\n" +
        "      border: 0px solid rgba(0, 0, 0, 0);\n" +
        "      border-radius: .25rem;\n" +
        "      margin-bottom: 1.5rem;\n" +
        "      box-shadow: 0 2px 6px 0 rgb(218 218 253), 0 2px 6px 0 rgb(206 206 238);\n" +
        "    }\n" +
        "\n" +
        "    .invoice table tfoot tr:last-child td {\n" +
        "      color: #0d6efd;\n" +
        "      font-size: 1.4em;\n" +
        "      border-top: 1px solid #0d6efd\n" +
        "    }\n" +
        "\n" +
        "    .invoice table tfoot tr td:first-child {\n" +
        "      border: none\n" +
        "    }\n" +
        "\n" +
        "    .invoice footer {\n" +
        "      width: 100%;\n" +
        "      text-align: center;\n" +
        "      color: #777;\n" +
        "      border-top: 1px solid #aaa;\n" +
        "      padding: 8px 0\n" +
        "    }\n" +
        "\n" +
        "    @media print {\n" +
        "      .invoice {\n" +
        "        font-size: 11px !important;\n" +
        "        overflow: hidden !important\n" +
        "      }\n" +
        "      .invoice footer {\n" +
        "        position: absolute;\n" +
        "        bottom: 10px;\n" +
        "        page-break-after: always\n" +
        "      }\n" +
        "      .invoice>div:last-child {\n" +
        "        page-break-before: always\n" +
        "      }\n" +
        "    }\n" +
        "\n" +
        "    .invoice main .notices {\n" +
        "      padding-left: 6px;\n" +
        "      border-left: 6px solid #0d6efd;\n" +
        "      background: #e7f2ff;\n" +
        "      padding: 10px;\n" +
        "    }\n" +
        "  </style>\n" +
        "</head>\n" +
        "<body>\n" +
        "  <div class=\"card\">\n" +
        "    <div class=\"card-body m-3 p-3\">\n" +
        "      <div id=\"invoice\">\n" +
        "        <div class=\"invoice overflow-auto m-1\">\n" +
        "          <div style=\"min-width: 600px\">\n" +
        "            <header>\n" +
        "              <div class=\"row\">\n" +
        "                  <a th:href =\"@{/salesInvoices/print/{invoiceId}/sent(invoiceId=${invoice.getId()})}\" type=\"button\" class=\"btn btn-primary \"><i class=\"fa fa-paper-plane mr-1\" ></i>Send Email</a>\n" +
        "                  <hr>\n" +
        "              </div>\n" +
        "              <div class=\"row\">\n" +
        "                <div class=\"col\">\n" +
        "                  <a href=\"javascript:;\">\n" +
        "                    <img src=\"assets/images/logo-icon.png\" width=\"80\" alt=\"\">\n" +
        "                  </a>\n" +
        "                </div>\n" +
        "                <div class=\"col company-details\">\n" +
        "                  <h2 class=\"name\" th:text=\"${company.getTitle()}\"></h2>\n" +
        "                  <div><a th:text=\"${company.getAddress().getAddressLine1()}\"></a></div>\n" +
        "                  <div><a th:text=\"${company.getAddress().getZipCode()}\"></a></div>\n" +
        "                  <div><a th:text=\"${company.getAddress().getState()}\"></a></div>\n" +
        "                  <div><a th:text=\"${company.getPhone()}\"></a></div>\n" +
        "                  <div><a th:text=\"${company.getWebsite()}\"></a></div>\n" +
        "                </div>\n" +
        "              </div>\n" +
        "            </header>\n" +
        "            <main>\n" +
        "              <div class=\"row contacts\">\n" +
        "                <div class=\"col invoice-to\">\n" +
        "                  <div class=\"text-gray-light\">INVOICE TO:</div>\n" +
        "                  <h2 class=\"to\" th:text=\"${invoice.getClientVendor().getClientVendorName()}\"></h2>\n" +
        "                  <div class=\"address\" th:text=\"${invoice.getClientVendor().getAddress().getAddressLine1()}\"></div>\n" +
        "                  <div class=\"address\" th:text=\"${invoice.getClientVendor().getAddress().getZipCode()} + ' / ' + ${invoice.getClientVendor().getAddress().getState()}\"></div>\n" +
        "                  <div class=\"email\"><a href=\"mailto:john@example.com\" th:text=\"${invoice.getClientVendor().getWebsite()}\"></a></div>\n" +
        "                </div>\n" +
        "              </div>\n" +
        "              <div class=\"col invoice-details\">\n" +
        "                <h1 class=\"invoice-id\" th:text=\"${invoice.getInvoiceNo()}\"></h1>\n" +
        "                <div class=\"date\" th:text=\"'Invoice Date: '+${invoice.getDate()}\"></div>\n" +
        "              </div>\n" +
        "              <table>\n" +
        "                <thead>\n" +
        "                <tr>\n" +
        "                  <th class=\"text-left\">#</th>\n" +
        "                  <th class=\"text-left\">Product</th>\n" +
        "                  <th class=\"text-right\">Price</th>\n" +
        "                  <th class=\"text-right\">Quantity</th>\n" +
        "                  <th class=\"text-right\">Tax</th>\n" +
        "                  <th class=\"text-right\">Total</th>\n" +
        "                </tr>\n" +
        "                </thead>\n" +
        "                <tbody>\n" +
        "                <tr th:each=\"invoiceProduct, iStat :${invoiceProducts}\">\n" +
        "                  <td class=\"total\" th:text=\"${iStat.count}\"></td>\n" +
        "                  <td class=\"text-left\"><h3 th:text=\"${invoiceProduct.getProduct().getName()}\"></h3></td>\n" +
        "                  <td class=\"unit\" th:text=\"'$ '+${invoiceProduct.getPrice()}\"></td>\n" +
        "                  <td class=\"unit\" th:text=\"${invoiceProduct.getQuantity()}\"></td>\n" +
        "                  <td class=\"unit\" th:text=\"'% '+${invoiceProduct.getTax()}\"></td>\n" +
        "                  <td class=\"total\" th:text=\"'$ '+${invoiceProduct.getTotal()}\"></td>\n" +
        "                </tr>\n" +
        "                </tbody>\n" +
        "                <tfoot>\n" +
        "                <tr>\n" +
        "                  <td></td>\n" +
        "                  <td colspan=\"2\"></td>\n" +
        "                  <td colspan=\"2\">SUBTOTAL</td>\n" +
        "                  <td th:text=\"'$ '+${invoice.getPrice()}\"></td>\n" +
        "                </tr>\n" +
        "                <tr>\n" +
        "                  <td></td>\n" +
        "                  <td colspan=\"2\"></td>\n" +
        "                  <td colspan=\"2\">TAX </td>\n" +
        "                  <td th:text=\"'$ '+${invoice.getTax()}\">510</td>\n" +
        "                </tr>\n" +
        "                <tr>\n" +
        "                  <td></td>\n" +
        "                  <td colspan=\"2\"></td>\n" +
        "                  <td colspan=\"2\">GRAND TOTAL</td>\n" +
        "                  <td th:text=\"'$ '+${invoice.getTotal()}\"></td>\n" +
        "                </tr>\n" +
        "                </tfoot>\n" +
        "              </table>\n" +
        "              <div class=\"notices\">\n" +
        "                <div>NOTICE:</div>\n" +
        "                <div class=\"notice\">A finance charge of 1.5% will be made on unpaid balances after 30 days.</div>\n" +
        "              </div>\n" +
        "            </main>\n" +
        "            <footer>Invoice was created on a computer and is valid without the signature and seal.</footer>\n" +
        "          </div>\n" +
        "        </div>\n" +
        "      </div>\n" +
        "    </div>\n" +
        "  </div>\n" +
        "  <button class=\"btn btn-primary scroll-top\" type=\"button\"><i class=\"ft-arrow-up\"></i></button>\n" +
        "  <!-- BEGIN PAGE LEVEL JS-->\n" +
        "  <script th:src=\"@{/js/dashboard2.js}\"></script>\n" +
        "  <script th:src=\"@{/assets/js/scripts.js}\"></script>\n" +
        "</body>\n" +
        "<!-- END : Body-->\n" +
        "</html>\n",file.getPath());
        fileSystemResource.getInputStream().close();


        return "redirect:/salesInvoices/print/{invoiceId}/sent";

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
