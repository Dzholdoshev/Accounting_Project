package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ReportingService;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportingServiceImpl implements ReportingService {

    private final InvoiceProductService invoiceProductService;
    private final InvoiceService invoiceService;

    public ReportingServiceImpl(InvoiceProductService invoiceProductService, InvoiceService invoiceService) {
        this.invoiceProductService = invoiceProductService;
        this.invoiceService = invoiceService;
    }


    @Override
    public List<InvoiceProductDto> getAllInvoiceProductDto() {
        return invoiceProductService.getAllByInvoiceStatusApprovedForCompany();

    }

    @Override
    public Map<String, BigDecimal> getAllMonthlyProfitLossData(Integer month,Integer year)  {
           Map<String,BigDecimal> MonthlyProfitLossDataMap = new HashMap<>();


            List<InvoiceDto> invoiceListByMonth = invoiceService.getAllInvoicesByInvoiceStatusAndMonth(InvoiceStatus.APPROVED, month,year);

            Long profitLoss = 0L;
            for (int J = 0; J < invoiceListByMonth.size(); J++) {
                List<InvoiceProductDto> invoiceProductdtoList = invoiceListByMonth.get(J).getInvoiceProducts();

                for (int j = 0; j < invoiceProductdtoList.size(); j++) {
                    profitLoss += invoiceProductdtoList.get(j).getProfitLoss().longValue();
                }
            }

            BigDecimal totalProfitLoss = BigDecimal.valueOf(profitLoss);
            MonthlyProfitLossDataMap.put("total", totalProfitLoss);

        return MonthlyProfitLossDataMap;
    }

    public Map<String, BigDecimal> getProfitLossByMonth()  {
        Map<String,BigDecimal> ProfitLossDataMap = new LinkedHashMap<>();
        LocalDate localDate=LocalDate.now();
        boolean test=true;
        int count=0;
        while(!localDate.isEqual(LocalDate.now().minusYears(2))){
            ProfitLossDataMap.put(localDate.getMonth().toString() +" "+ localDate.getYear()
                    , getAllMonthlyProfitLossData(localDate.getMonthValue(), localDate.getYear()).get("total"));
      localDate= localDate.minusMonths(1);
        }

        return ProfitLossDataMap;

    }

    }


