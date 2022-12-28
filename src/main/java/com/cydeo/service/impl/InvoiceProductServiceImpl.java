package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.InvoiceProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {

    private final InvoiceProductRepository invoiceProductRepository;
    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, InvoiceRepository invoiceRepository, MapperUtil mapperUtil) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
    }


    @Override
    public List<InvoiceProductDto> listAllInvoiceProduct() {
      List<InvoiceProductDto> InvoiceProductDto=invoiceProductRepository.findAllByIsDeleted(false)
              .stream().map(invoiceP->mapperUtil.convert(invoiceP, new InvoiceProductDto()))
              .collect(Collectors.toList());
      return InvoiceProductDto;
    }

    @Override
    public List<InvoiceProductDto> findByInvoiceId(Long id) {
        return invoiceProductRepository.findInvoiceProductByInvoice_IdAndIsDeleted(id, false).stream()
                .map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceProductDto createInvoiceProducts(Long id, InvoiceProductDto invoiceProductDto) {
        InvoiceDto invoiceDto = mapperUtil.convert(invoiceRepository.findByIdAndIsDeleted(id, false).get(), new InvoiceDto());
        invoiceProductDto.setInvoice(invoiceDto);
        // Call productService to get quantity?
        // InvoiceProductDto.setRemainingQuantity();

        invoiceProductRepository.save(mapperUtil.convert(invoiceProductDto, new InvoiceProduct()));
        return invoiceProductDto;
    }
    public void delete(Long invoiceId, Long invoiceProductId) {
        List<InvoiceProduct> invoiceProductList= invoiceProductRepository.findInvoiceProductByInvoice_IdAndIsDeleted(invoiceId, false);
        InvoiceProduct invoiceProduct = invoiceProductList.stream().filter(p -> p.getId().equals(invoiceProductId)).findFirst().orElseThrow();
        invoiceProduct.setIsDeleted(true);
        invoiceProductRepository.save(invoiceProduct);
    }

}
