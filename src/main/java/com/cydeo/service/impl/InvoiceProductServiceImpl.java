package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.entity.Product;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {

    private final InvoiceProductRepository invoiceProductRepository;
    private final InvoiceService invoiceService;
    private final MapperUtil mapperUtil;
  //  private final ProductService productService;


    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, @Lazy InvoiceService invoiceService, MapperUtil mapperUtil) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.invoiceService = invoiceService;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public InvoiceProductDto findInvoiceProductById(long id) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(id).orElseThrow();
        return mapperUtil.convert(invoiceProduct, new InvoiceProductDto());
    }


    @Override
    public List<InvoiceProductDto> getInvoiceProductsOfInvoice(Long invoiceId) {
        return invoiceProductRepository.findAllByInvoice_Id(invoiceId).stream().
                map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Long invoiceId, InvoiceProductDto invoiceProductDto) {
        InvoiceDto invoiceDto = mapperUtil.convert(invoiceService.findInvoiceById(invoiceId), new InvoiceDto());
        invoiceProductDto.setInvoice(invoiceDto);
        //remaining quantity
        //profitLoss
        //price
        //tax

        invoiceProductRepository.save(mapperUtil.convert(invoiceProductDto, new InvoiceProduct()));
    }

    @Override
    public void delete(Long invoiceProductId) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findInvoiceProductById(invoiceProductId);
        invoiceProduct.setIsDeleted(true);
        invoiceProductRepository.save(invoiceProduct);
    }

    @Override
    public void completeApprovalProcedures(Long invoiceId, InvoiceType type) {
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAllByInvoice_Id(invoiceId);
        if(type ==InvoiceType.SALES){
            for (InvoiceProduct eachInvoiceProduct : invoiceProductList) {
               // if(eachInvoiceProduct.getProduct().getQuantityInStock()>=eachInvoiceProduct.getQuantity())
                //waiting for product entity
            }
        }
    }

    @Override
    public boolean checkProductQuantity(InvoiceProductDto salesInvoiceProduct) {
        return salesInvoiceProduct.getProduct().getQuantityInStock() >= salesInvoiceProduct.getQuantity();
    }

    @Override
    public List<InvoiceProduct> findInvoiceProductsByInvoiceTypeAndProductRemainingQuantity(InvoiceType type, Product product, Integer remainingQuantity) {
        return invoiceProductRepository.findInvoiceProductsByInvoiceInvoiceTypeAndProductAndRemainingQuantityNotOrderByIdAsc(type, product, remainingQuantity);
    }

    @Override
    public List<InvoiceProductDto> findAllInvoiceProductsByProductId(Long id) {
        return invoiceProductRepository.findAllInvoiceProductByProductId(id).stream()
                .map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .collect(Collectors.toList());
    }

}