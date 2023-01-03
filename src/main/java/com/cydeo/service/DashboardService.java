package com.cydeo.service;


import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


public interface DashboardService {

    Map<String, BigDecimal> getSummaryNumbers()throws Exception;

    CurrencyDto getExchangeRates();


}
