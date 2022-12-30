package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Company;
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
        Invoice invoice = invoiceRepository.findInvoiceById(id);
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public List<InvoiceDto> getAllInvoicesOfCompany(InvoiceType invoiceType) throws Exception {

        User user = mapperUtil.convert(securityService.getLoggedInUser(), new User());
        Company company=user.getCompany();
        List<Invoice> PurchaseInvoicesList = invoiceRepository.findInvoicesByCompanyAndInvoiceType(company, InvoiceType.PURCHASE);

            return PurchaseInvoicesList.stream().map(invoice -> {
                InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
                invoiceDto.setPrice(invoicePrice(invoiceDto));
                invoiceDto.setTax(invoiceTax(invoiceDto));
                invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto.getId()));

                return invoiceDto;
            }).sorted(Comparator.comparing(InvoiceDto::getInvoiceNo).reversed()).collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDto> getAllInvoicesByInvoiceStatus(InvoiceStatus status) {
        User user = mapperUtil.convert(securityService.getLoggedInUser(), new User());
        Company company=user.getCompany();
      List<Invoice> invoiceList=  invoiceRepository.findInvoicesByCompanyAndInvoiceStatus(company, status);

        return invoicesList.stream().map(invoice -> {
            InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
            invoiceDto.setPrice(invoicePrice(invoiceDto));
            invoiceDto.setTax(invoiceTax(invoiceDto));
            invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto.getId()));

            return invoiceDto;}).collect(Collectors.toList());

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
    public InvoiceDto update(Long id, InvoiceDto invoiceDto) {
       Invoice invoice= invoiceRepository.findInvoiceById(id);
        Invoice updatedInvoice = mapperUtil.convert(invoiceDto, new Invoice());
        invoice.setClientVendor(updatedInvoice.getClientVendor());
        invoiceRepository.save(invoice);
        return invoiceDto;
    }

    @Override
    public void approve(Long id) {
        Invoice invoice = invoiceRepository.findInvoiceById(id);
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        //Change product quantities
        // productService.update(invoice.getInvoiceProduct());  to update stock values?
        invoiceRepository.save(invoice);
    }


    @Override
    public InvoiceDto printInvoice(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findInvoiceById(id);
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
        InvoiceDto invoiceDto = mapperUtil.convert(invoiceRepository.findById(id), new InvoiceDto());
        BigDecimal price = invoiceDto.getInvoiceProducts().stream().map(InvoiceProductDto::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = getTotalTaxOfInvoice(id);
        BigDecimal total = tax.add(price);
        return total;
    }

    @Override
    public BigDecimal getTotalTaxOfInvoice(Long id) { // Sum of the tax of the Invoice Product
        InvoiceDto invoiceDto = mapperUtil.convert(invoiceRepository.findById(id), new InvoiceDto());

        BigDecimal tax = invoiceDto.getInvoiceProducts().stream().map(InvoiceProductDto::getTax).reduce(0, Integer::sum);
        return tax;
    }

    @Override
    public BigDecimal getProfitLossOfInvoice(Long id) {
        return null;
    }

    @Override
    public boolean checkIfInvoiceExist(Long clientVendorId) {
        return false;
    }





    public String InvoiceNo(InvoiceType invoiceType) {
        Long id = invoiceRepository.getMaxId(invoiceType);
        String InvoiceNo = "";

        if (invoiceType.value().equals("Purchase")) {
            InvoiceNo = "P-" + String.format("%03d", id + 1);

        } else {
            InvoiceNo = "S-" + String.format("%03d", id + 1);

        }
        return InvoiceNo;
    }
}


