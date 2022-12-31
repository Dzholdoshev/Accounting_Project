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
import com.cydeo.service.SecurityService;
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
    // private final ProductService productService;
    private final SecurityService securityService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil, SecurityService securityService) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
    }


    @Override
    public InvoiceDto findInvoiceById(long id) {
        Invoice invoice = invoiceRepository.findInvoiceById(id);
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public List<InvoiceDto> getAllInvoicesOfCompany(InvoiceType invoiceType) throws Exception {

        User user = mapperUtil.convert(securityService.getLoggedInUser(), new User());
        Company company = user.getCompany();

        List<Invoice> PurchaseInvoicesList = invoiceRepository.findInvoicesByCompanyAndInvoiceType(company, InvoiceType.PURCHASE);

        return PurchaseInvoicesList.stream().map(invoice -> {
            InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
            // invoiceDto.setPrice(invoicePrice(invoiceDto));
            invoiceDto.setTax(getTotalTaxOfInvoice(invoiceDto.getId()).intValue());
            invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto.getId()));

            return invoiceDto;
        }).sorted(Comparator.comparing(InvoiceDto::getInvoiceNo).reversed()).collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDto> getAllInvoicesByInvoiceStatus(InvoiceStatus status) {
        User user = mapperUtil.convert(securityService.getLoggedInUser(), new User());

        Company company = user.getCompany();
        List<Invoice> invoiceList = invoiceRepository.findInvoicesByCompanyAndInvoiceStatus(company, status);

        return invoiceList.stream().map(invoice -> {
            InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
            // invoiceDto.setPrice(invoicePrice(invoiceDto));
            invoiceDto.setTax(getTotalTaxOfInvoice(invoiceDto.getId()).intValue());
            invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto.getId()));

            return invoiceDto;
        }).collect(Collectors.toList());

    }

    @Override
    public InvoiceDto getNewInvoice(InvoiceType invoiceType) throws Exception {

        Long companyId= securityService.getLoggedInUser().getCompany().getId();

        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(InvoiceNo(InvoiceType.PURCHASE, comapanyId));
        invoice.setDate(LocalDate.now());
        invoice.setInvoiceType(invoiceType);
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public InvoiceDto save(InvoiceDto invoiceDto, InvoiceType invoiceType) {

        User user = mapperUtil.convert(securityService.getLoggedInUser(), new User());
        if (invoiceType.getValue().equals("Purchase")) {
            invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        }
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());
        invoice.setCompany(user.getCompany());
        invoiceRepository.save(invoice);

        return invoiceDto;
    }

    @Override
    public InvoiceDto update(Long id, InvoiceDto invoiceDto) {
        Invoice invoice = invoiceRepository.findInvoiceById(id);
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
        InvoiceDto invoiceDto = mapperUtil.convert(invoiceRepository.findInvoiceById(id), new InvoiceDto());
        // invoiceDto.setPrice();
        invoiceDto.setTax(getTotalTaxOfInvoice(invoiceDto.getId()).intValue());
        invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto.getId()));
        return invoiceDto;
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

        Integer tax = invoiceDto.getInvoiceProducts().stream().map(InvoiceProductDto::getTax).reduce(0, Integer::sum);
        return BigDecimal.valueOf(tax);
    }

    @Override
    public BigDecimal getProfitLossOfInvoice(Long id) {

        //Total price of the invoice subtracted by cost of products, only a loss if negative?
        return null;
    }

    @Override
    public boolean checkIfInvoiceExist(Long clientVendorId) {
        List<Invoice> invoiceList = invoiceRepository.findAll();
        return invoiceList.stream().anyMatch(invoice -> invoice.getClientVendor().getId().equals(clientVendorId));
    }


    public String InvoiceNo(InvoiceType invoiceType, Long companyId) {
        Long id = invoiceRepository.getMaxId(invoiceType, companyId );
        String InvoiceNo = "";

        if (invoiceType.getValue().equals("Purchase")) {
            InvoiceNo = "P-" + String.format("%03d", id + 1);

        } else {
            InvoiceNo = "S-" + String.format("%03d", id + 1);

        }
        return InvoiceNo;
    }
}


