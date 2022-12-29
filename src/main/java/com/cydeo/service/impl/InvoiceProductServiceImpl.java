package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.InvoiceProduct;
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
    private final ProductService productService;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, @Lazy InvoiceService invoiceService, MapperUtil mapperUtil, ProductService productService) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.invoiceService = invoiceService;
        this.mapperUtil = mapperUtil;
        this.productService = productService;
    }

    @Override
    public List<InvoiceProductDto> listAllInvoiceProduct() {
      List<InvoiceProductDto> InvoiceProductDto=invoiceProductRepository.findAllByIsDeleted(false)
              .stream().map(invoiceP->mapperUtil.convert(invoiceP, new InvoiceProductDto()))
              .collect(Collectors.toList());
      return InvoiceProductDto;
    }

    @Override
    public List<InvoiceProductDto> findAllInvoiceProductsByProductId(long id) {
        return invoiceProductRepository.findInvoiceProductByInvoice_IdAndIsDeleted(id, false).stream()
                .map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceProductDto save(Long id, InvoiceProductDto invoiceProductDto) {
        InvoiceDto invoiceDto = mapperUtil.convert(invoiceService.findInvoiceById(id), new InvoiceDto());
        invoiceProductDto.setInvoice(invoiceDto);
        // Call productService to get quantity?
        // InvoiceProductDto.setRemainingQuantity();

        invoiceProductRepository.save(mapperUtil.convert(invoiceProductDto, new InvoiceProduct()));
        return invoiceProductDto;
    }

    @Override
    public List<InvoiceProductDto> findByInvoiceTypesAndProductRemainingQuantity(InvoiceType invoiceType) {
        return null;
    }

    @Override
    public void delete(Long invoiceId, Long invoiceProductId) {
        List<InvoiceProduct> invoiceProductList= invoiceProductRepository.findInvoiceProductByInvoice_IdAndIsDeleted(invoiceId, false);
        InvoiceProduct invoiceProduct = invoiceProductList.stream().filter(p -> p.getId().equals(invoiceProductId)).findFirst().orElseThrow();
        invoiceProduct.setIsDeleted(true);
        invoiceProductRepository.save(invoiceProduct);
    }

}
