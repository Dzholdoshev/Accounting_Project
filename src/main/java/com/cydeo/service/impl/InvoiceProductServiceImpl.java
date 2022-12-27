package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.InvoiceProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {

    private final InvoiceProductRepository invoiceProductRepository;
    private final MapperUtil mapperUtil;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, MapperUtil mapperUtil) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.mapperUtil = mapperUtil;
    }


    @Override
    public List<InvoiceProductDto> listAllInvoiceProduct() {
      List<InvoiceProductDto> InvoiceProductDto=invoiceProductRepository.findAll()
              .stream().map(invoiceP->mapperUtil.convert(invoiceP, new InvoiceProductDto()))
              .collect(Collectors.toList());
      return InvoiceProductDto;
    }

    @Override
    public List<InvoiceProductDto> findByInvoiceNo(String invoiceNo) {
//      return  invoiceProductRepository.findByInvoiceNumber(invoiceNo).stream()
//                .map(invoiceP->mapperUtil.convert(invoiceP, new InvoiceProductDto()))
//              .collect(Collectors.toList());
        return null;
   }

    @Override
    public BigDecimal findPriceByInvoiceNo(String invoiceNo) {

//        return  invoiceProductRepository.findPriceByInvoiceNumber(invoiceNo).stream()
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return null;
    }
}
