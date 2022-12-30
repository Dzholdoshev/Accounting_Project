package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.User;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;
    private final ProductService productService;
    private final SercurityService sercurityService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil, ProductService productService, SercurityService sercurityService) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
        this.productService = productService;
        this.sercurityService = sercurityService;
    }

    @Override
    public InvoiceDto findInvoiceById(long id) {
        Invoice invoice = invoiceRepository.findByIdAndIsDeleted(id, false);
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public List<InvoiceDto> getAllInvoicesOfCompany(InvoiceType invoiceType) throws Exception {

        User user = mapperUtil.convert(securityService.getLoggedInUser(), new User());

        List<Invoice> invoicesList = invoiceRepository.findAllByInvoiceTypeAndIsDeleted(invoiceType, false);

        if (invoiceType.getValue().equals("Purchase")) {

            return invoicesList.stream().map(invoice -> {
                InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
                invoiceDto.setPrice(invoicePrice(invoiceDto));
                invoiceDto.setTax(invoiceTax(invoiceDto));
                invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto));

                return invoiceDto;
            }).sorted(Comparator.comparing(InvoiceDto::getInvoiceNo).reversed()).collect(Collectors.toList());
        } else if (invoiceType.getValue().equals("Sales")) {
            return invoicesList.stream().map(invoice -> {
                InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
                invoiceDto.setPrice(invoicePrice(invoiceDto));
                invoiceDto.setTax(invoiceTax(invoiceDto));
                invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto));

                return invoiceDto;
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<InvoiceDto> getAllInvoicesByInvoiceStatus(InvoiceStatus status) {
        return null;
    }

    @Override
    public InvoiceDto getNewInvoice(InvoiceType invoiceType) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(InvoiceNo(InvoiceType.PURCHASE));
        invoice.setDate(LocalDate.now());
        invoice.setInvoiceType(invoiceType);
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public InvoiceDto save(InvoiceDto invoiceDto, InvoiceType invoiceType) {

        User user = mapperUtil.convert(securityService.getLoggedInUser(), new User());
        if (InvoiceType.getValue.equals("Purchase")) {
            invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        }
            Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());
            invoice.setCompany(user.getCompany());
            invoiceRepository.save(invoice);

        return invoiceDto;
    }

    @Override
    public InvoiceDto printInvoice(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findByIdAndIsDeleted(id, false);
        if (invoice.getInvoiceStatus().getValue().equals("Awaiting Approval")) {
            invoice.setIsDeleted(true);
            invoiceRepository.save(invoice);
        }
    }

    @Override
    public List<InvoiceDto> getLastThreeInvoices() {
        //Ilhan
        return null;
    }

    @Override
    public BigDecimal getTotalPriceOfInvoice(Long id) { // Invoice tax+ Invoice price
        InvoiceDto invoiceDto=  mapperUtil.convert(invoiceRepository.findById(id), new InvoiceDto());
        BigDecimal price=invoiceDto.getInvoiceProducts().stream()
                .map(InvoiceProductDto::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = getTotalTaxOfInvoice(id);
        BigDecimal total= tax.add(price);
        return total;
    }

    @Override
    public BigDecimal getTotalTaxOfInvoice(Long id) { // Sum of the tax of the Invoice Product
        InvoiceDto invoiceDto=  mapperUtil.convert(invoiceRepository.findById(id), new InvoiceDto());

        BigDecimal tax=invoiceDto.getInvoiceProducts().stream()
                .map(InvoiceProductDto::getTax)
                .reduce(0, Integer::sum);
        return tax;
    }




    @Override
    public void updateInvoice(InvoiceDto invoiceDto) {

        Invoice updatedInvoice = mapperUtil.convert(invoiceDto, new Invoice());
        Invoice invoice = invoiceRepository.findByIdAndIsDeleted(invoiceDto.getId(), false);
        invoice.setCompany(updatedInvoice.getCompany());
        invoiceRepository.save(invoice);

    }



    @Override
    public void approveInvoice(Long id) {
        Invoice invoice = invoiceRepository.findByIdAndIsDeleted(id, false);
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        //Change product quantities
        // productService.update(invoice.getInvoiceProduct());  to update stock values?
        invoiceRepository.save(invoice);
    }


    public String InvoiceNo(InvoiceType invoiceType) {
        Long id = invoiceRepository.getMaxId(invoiceType);
        String InvoiceNo = "";

        if (invoiceType.value.equals("Purchase")) {
            InvoiceNo = "P-" + String.format("%03d", id + 1);

        } else {
            InvoiceNo = "S-" + String.format("%03d", id + 1);

        }
        return InvoiceNo;
    }
}

// check if invoice exists
