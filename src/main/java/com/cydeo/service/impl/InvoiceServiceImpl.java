package com.cydeo.service.impl;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.*;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.NotEnoughProductException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.SecurityService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;
    private final InvoiceProductService invoiceProductService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil, SecurityService securityService, @Lazy InvoiceProductService invoiceProductService) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
        this.invoiceProductService = invoiceProductService;
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
        List<Invoice> PurchaseInvoicesList = invoiceRepository.findInvoicesByCompanyAndInvoiceTypeAndIsDeleted(company, invoiceType, false);

        return PurchaseInvoicesList.stream().map(invoice -> {
            InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
            invoiceDto.setTax(getTotalTaxOfInvoice(invoice.getId()));
            invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto.getId()));
            invoiceDto.setInvoiceProducts(invoiceProductService.getInvoiceProductsOfInvoice(invoiceDto.getId()));
            invoiceDto.setPrice(invoiceDto.getTotal().subtract(invoiceDto.getTax()));
            return invoiceDto;
        }).sorted(Comparator.comparing(InvoiceDto::getInvoiceNo).reversed()).collect(Collectors.toList());

    }

    @Override
    public List<InvoiceDto> getAllInvoicesByInvoiceStatus(InvoiceStatus status) {
        User user = mapperUtil.convert(securityService.getLoggedInUser(), new User());

        Company company = user.getCompany();
        List<Invoice> invoiceList = invoiceRepository.findInvoicesByCompanyAndInvoiceStatusAndIsDeleted(company, status, false);

        return invoiceList.stream().map(invoice -> {
            InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
            Long invoiceId = invoiceDto.getId();
            invoiceDto.setTax(getTotalTaxOfInvoice(invoiceId));
            invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceId));
            invoiceDto.setPrice(invoiceDto.getTotal().subtract(invoiceDto.getTax()));

            return invoiceDto;
        }).collect(Collectors.toList());

    }

    @Override
    public InvoiceDto getNewInvoice(InvoiceType invoiceType) throws Exception {

        Long companyId = securityService.getLoggedInUser().getCompany().getId();
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(InvoiceNo(invoiceType, companyId));
        invoice.setDate(LocalDate.now());
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public InvoiceDto save(InvoiceDto invoiceDto, InvoiceType invoiceType) {

        User user = mapperUtil.convert(securityService.getLoggedInUser(), new User());
        invoiceDto.setInvoiceType(invoiceType);
        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());
        invoice.setCompany(user.getCompany());
        invoice.setIsDeleted(false);
        invoiceRepository.save(invoice);
        invoiceDto.setId(invoice.getId());
        return invoiceDto;
    }

    @Override
    public InvoiceDto update(Long id, InvoiceDto invoiceDto) {
        Invoice updatedInvoice = mapperUtil.convert(invoiceDto, new Invoice());
        Invoice invoice = invoiceRepository.findInvoiceById(id);
        invoice.setClientVendor(updatedInvoice.getClientVendor());
        invoiceRepository.save(invoice);
        return invoiceDto;
    }

    @Override
    @Transactional
    public void approve(Long invoiceId) throws NotEnoughProductException {
        Invoice invoice = invoiceRepository.findInvoiceById(invoiceId);
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoice.setDate(LocalDate.now());
        invoiceProductService.completeApprovalProcedures(invoiceId, invoice.getInvoiceType());
        invoiceRepository.save(invoice);
    }

    @Override
    public InvoiceDto printInvoice(Long id) {
        InvoiceDto invoiceDto = mapperUtil.convert(invoiceRepository.findInvoiceById(id), new InvoiceDto());
        invoiceDto.setInvoiceProducts(invoiceProductService.getInvoiceProductsOfInvoice(id));
        invoiceDto.setTax(getTotalTaxOfInvoice(invoiceDto.getId()));
        invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto.getId()));
        invoiceDto.setPrice(invoiceDto.getTotal().subtract(invoiceDto.getTax()));
        return invoiceDto;
    }

    @Override
    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findInvoiceById(id);
        if (invoice.getInvoiceStatus().getValue().equals("Awaiting Approval")) {
            invoice.setIsDeleted(true);
            invoiceProductService.getInvoiceProductsOfInvoice(id).stream().
                    peek(invoiceProductDto -> invoiceProductService.delete(invoiceProductDto.getId())).collect(Collectors.toList());
            invoiceRepository.save(invoice);
        }
    }

    @Override
    public List<InvoiceDto> getLastThreeInvoices() { //my changes ilhan

        Company company = mapperUtil.convert(securityService.getLoggedInUser().getCompany(), new Company());
        return invoiceRepository.findInvoicesByCompanyAndInvoiceStatusAndIsDeletedOrderByDateDesc(company, InvoiceStatus.APPROVED, false)
                .stream()
                .limit(3)
                .map(each -> mapperUtil.convert(each, new InvoiceDto()))
                .peek(this::calculateInvoiceDetails)
                .collect(Collectors.toList());
    }

    private void calculateInvoiceDetails(InvoiceDto invoiceDto) {   // my changes ilhan

        invoiceDto.setTax(getTotalTaxOfInvoice(invoiceDto.getId()));
        invoiceDto.setPrice(getTotalPriceOfInvoice(invoiceDto.getId()).subtract(invoiceDto.getTax()));
        invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto.getId()));
    }

    @Override
    public BigDecimal getTotalPriceOfInvoice(Long invoiceId) { // Invoice tax+ Invoice price
        List<InvoiceProductDto> listOfInvoiceProducts = invoiceProductService.getInvoiceProductsOfInvoice(invoiceId);
        if (listOfInvoiceProducts != null) {
            BigDecimal price = listOfInvoiceProducts.stream().map(invoiceProduct -> {
                        BigDecimal priceOfProduct = invoiceProduct.getPrice();
                        Integer quantityOfProduct = invoiceProduct.getQuantity();
                        return priceOfProduct.multiply(BigDecimal.valueOf(quantityOfProduct));
                    }
            ).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal tax = getTotalTaxOfInvoice(invoiceId);
            BigDecimal total = tax.add(price);
            return total;
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalTaxOfInvoice(Long invoiceId) { // Sum of the tax of the Invoice Product

        List<InvoiceProductDto> listOfInvoiceProducts = invoiceProductService.getInvoiceProductsOfInvoice(invoiceId);
        if (listOfInvoiceProducts != null) {

            return listOfInvoiceProducts.stream().map(invoiceProductDto -> {
                BigDecimal price = invoiceProductDto.getPrice();
                Integer quantityOfProduct = invoiceProductDto.getQuantity();
                price = price.multiply(BigDecimal.valueOf(quantityOfProduct));
                BigDecimal tax = BigDecimal.valueOf(invoiceProductDto.getTax()).divide(BigDecimal.valueOf(100));
                return price.multiply(tax);
            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getProfitLossOfInvoice(Long id) {
       InvoiceDto invoiceDto= findInvoiceById(id);
     return invoiceProductService.getInvoiceProductsOfInvoice(invoiceDto.getId()).stream()
               .map(InvoiceProductDto::getProfitLoss)
               .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    @Override
    public boolean checkIfInvoiceExist(Long clientVendorId) {
        List<Invoice> invoiceList = invoiceRepository.findAll();
        return invoiceList.stream().anyMatch(invoice -> invoice.getClientVendor().getId().equals(clientVendorId));
    }


    public String InvoiceNo(InvoiceType invoiceType, Long companyId) {
        Long id = invoiceRepository.countAllByInvoiceTypeAndCompanyId(invoiceType, companyId);
        String InvoiceNo = "";

        if (invoiceType.getValue().equals("Purchase")) {
            InvoiceNo = "P-" + String.format("%03d", id + 1);
        } else {
            InvoiceNo = "S-" + String.format("%03d", id + 1);
        }
        return InvoiceNo;
    }


}


