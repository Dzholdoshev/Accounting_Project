package com.cydeo.service.impl;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.User;
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
    // private final ProductService productService;
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

        List<Invoice> PurchaseInvoicesList = invoiceRepository.findInvoicesByCompanyAndInvoiceType(company, invoiceType);

        return PurchaseInvoicesList.stream().map(invoice -> {
            InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());

            invoiceDto.setTax(getTotalTaxOfInvoice(invoice.getId()));
            invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto.getId()));
            invoiceDto.setInvoiceProducts(invoiceProductService.getInvoiceProductsOfInvoice(invoiceDto.getId()));
            invoiceDto.setClientVendor(mapperUtil.convert(invoice.getClientVendor(), new ClientVendorDto()));
            invoiceDto.setPrice(invoiceDto.getTotal().subtract(invoiceDto.getTax()));

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
            invoiceDto.setTax(getTotalTaxOfInvoice(invoiceDto.getId()));
            invoiceDto.setTotal(getTotalPriceOfInvoice(invoiceDto.getId()));

            return invoiceDto;
        }).collect(Collectors.toList());

    }

    @Override
    public InvoiceDto getNewInvoice(InvoiceType invoiceType) throws Exception {

        Long companyId = securityService.getLoggedInUser().getCompany().getId();

        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(InvoiceNo(invoiceType, companyId));
        invoice.setDate(LocalDate.now());
        invoice.setInvoiceType(invoiceType);
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public InvoiceDto save(InvoiceDto invoiceDto, InvoiceType invoiceType) {

        User user = mapperUtil.convert(securityService.getLoggedInUser(), new User());
        invoiceDto.setInvoiceType(invoiceType);
        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());
        invoice.setCompany(user.getCompany());
        invoice.setClientVendor(mapperUtil.convert(invoiceDto.getClientVendor(), new ClientVendor()));
        invoiceRepository.save(invoice);
        invoiceDto.setId(invoice.getId());
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
    @Transactional
    public void approve(Long invoiceId) throws NotEnoughProductException {

        Invoice invoice = invoiceRepository.findInvoiceById(invoiceId);
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        //Change product quantities
        // productService.update(invoice.getInvoiceProduct());  to update stock values?

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
            invoiceRepository.save(invoice);
        }
    }

    @Override
    public List<InvoiceDto> getLastThreeInvoices() {
        //Ilhan
        return null;
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

        //Total price of the invoice subtracted by cost of products, only a loss if negative?
        return null;
    }

    @Override
    public boolean checkIfInvoiceExist(Long clientVendorId) {
        List<Invoice> invoiceList = invoiceRepository.findAll();
        return invoiceList.stream().anyMatch(invoice -> invoice.getClientVendor().getId().equals(clientVendorId));
    }


    public String InvoiceNo(InvoiceType invoiceType, Long companyId) {
        Long id = invoiceRepository.getMaxId(invoiceType, companyId);
        String InvoiceNo = "";

        if (invoiceType.getValue().equals("Purchase")) {
            InvoiceNo = "P-" + String.format("%03d", id + 1);
        } else {
            InvoiceNo = "S-" + String.format("%03d", id + 1);
        }
        return InvoiceNo;
    }


}


