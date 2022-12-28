package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceProductService invoiceProductService;
    private final MapperUtil mapperUtil;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, InvoiceProductService invoiceProductService, MapperUtil mapperUtil) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceProductService = invoiceProductService;
        this.mapperUtil = mapperUtil;
    }


    @Override
    public List<InvoiceDto> listAllInvoices(InvoiceType invoiceType) {
        List<Invoice> invoicesList = invoiceRepository.findAllByInvoiceType(invoiceType);
        return invoicesList.stream().map(invoice -> {
            InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
            BigDecimal price = invoiceDto.getInvoiceProducts().stream().map(InvoiceProductDto::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            invoiceDto.setPrice(price);
           // invoiceDto.setTax();
           // invoiceDto.setTotal();

            return invoiceDto;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateInvoice(InvoiceDto invoiceDto) {

        Invoice updatedInvoice = mapperUtil.convert(invoiceDto, new Invoice());
        Invoice invoice = invoiceRepository.findById(invoiceDto.getId()).get();
        invoice.setCompany(updatedInvoice.getCompany());

        invoiceProductService.updateProducts(invoiceDto.getInvoiceNo, invoiceDto.getInvoiceProducts());
        invoiceRepository.save(invoice);

    }

    @Override
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id).get();
        if (invoice.getInvoiceStatus().getValue().equals("Awaiting Approval")) {
            invoice.setIsDeleted(true);
            invoiceRepository.save(invoice);
        }
        //InvoiceProduct? how will the quanitity change?
    }

    @Override
    public void approveInvoice(Long id) {
        Invoice invoice=invoiceRepository.findById(id).get();
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoiceRepository.save(invoice);
    }

    @Override
    public InvoiceDto create(InvoiceDto invoiceDto) {
        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());
        invoiceRepository.save(invoice);
        return invoiceDto;
    }

    @Override
    public InvoiceDto findById(Long id) {
      Invoice invoice= invoiceRepository.findById(id).get();
      return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public InvoiceDto createNewInvoiceDto() {
        Invoice invoice= new Invoice();
        invoice.setInvoiceNo("P-"+invoice.getId());
        invoice.setDate(LocalDate.now());
        return mapperUtil.convert(invoice, new InvoiceDto());
    }
}
