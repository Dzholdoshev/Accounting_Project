package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    public List<InvoiceDto> listAllInvoices() {
        List<Invoice> invoicesList = invoiceRepository.findAll();
        return invoicesList.stream().map(invoice -> {
                    InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
                    BigDecimal price = invoiceProductService.findPriceByInvoiceNo(invoice.getInvoiceNo());
                    invoiceDto.setPrice(price);
    //tax

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
        }

    }

    @Override
    public void approveInvoice(Long id) {

    }

    @Override
    public InvoiceDto create(InvoiceDto invoiceDto) {
        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
     return invoiceDto;
    }

    @Override
    public InvoiceDto findById(Long id) {
      Invoice invoice= invoiceRepository.findById(id).get();
      return mapperUtil.convert(invoice, new InvoiceDto());
    }


}
