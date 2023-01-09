package com.cydeo.controller;

import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.ReportingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/reports")
public class ReportingController {

    private final ReportingService reportingService;
    private final InvoiceProductService invoiceProductService;

    public ReportingController(ReportingService reportingService, InvoiceProductService invoiceProductService) {
        this.reportingService = reportingService;
        this.invoiceProductService = invoiceProductService;
    }

    @GetMapping("/profitLossData")
    public String listLossData(Model model){
        Map <String, BigDecimal> test = new HashMap<>();
        test.put("month", BigDecimal.ZERO);

       model.addAttribute("monthlyProfitLossDataMap",test);
                return "report/profit-loss-report";
    }
    @GetMapping("/stockData")
    public String listAllData(Model model){
        // model.addAttribute("invoiceProducts",)
        return "report/stock-report";
    }

}
