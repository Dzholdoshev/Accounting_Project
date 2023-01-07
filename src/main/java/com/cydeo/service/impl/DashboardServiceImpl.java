package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.DashboardService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {



    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;
    private final CompanyService companyService;
    //private final CurrencyExchangeClient client;

    public DashboardServiceImpl(InvoiceService invoiceService,/*, CurrencyExchangeClient client*/InvoiceServiceImpl invoiceServiceImpl, InvoiceRepository invoiceRepository, CompanyService companyService, MapperUtil mapperUtil, SecurityService securityService) {
        this.invoiceService = invoiceService;
        //this.client = client;
        this.invoiceRepository = invoiceRepository;
        this.companyService = companyService;
    }

    public Map<String, BigDecimal> getSummaryNumbers()throws Exception{

        Map<String,BigDecimal> getSummaryNumbers = new HashMap<>();

        BigDecimal totalCost = invoiceService.getAllInvoicesByInvoiceStatus(InvoiceStatus.APPROVED).stream().filter(
                invoiceDto -> invoiceDto.getInvoiceType().equals(InvoiceType.PURCHASE))
                .map(InvoiceDto::getTotal).reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal totalSales = invoiceService.getAllInvoicesByInvoiceStatus(InvoiceStatus.APPROVED).stream().filter(
                        invoiceDto -> invoiceDto.getInvoiceType().equals(InvoiceType.SALES))
                .map(InvoiceDto::getTotal).reduce(BigDecimal.ZERO,BigDecimal::add);


                getSummaryNumbers.put("totalCost",totalCost);
                getSummaryNumbers.put("totalSales",totalSales);
                getSummaryNumbers.put("profitLoss",new BigDecimal(100));  //for previewing

                        /*invoiceService.getTotalPriceOfInvoice(
                        invoiceRepository.findInvoicesByCompanyAndInvoiceStatusAndIsDeleted(
                        new Company(), InvoiceStatus.APPROVED,false)))*/
        return getSummaryNumbers;
    }


    //CurrencyDto getExchangeRates();




}
