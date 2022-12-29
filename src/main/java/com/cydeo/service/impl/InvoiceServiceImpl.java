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
import com.cydeo.service.SecurityService;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Statement;
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
    public List<InvoiceDto> listAllInvoices(InvoiceType invoiceType) {

        User user = mapperUtil.convert(securityService.getLoggedInUser(), new User());

        List<Invoice> invoicesList = invoiceRepository.findAllByInvoiceTypeAndIsDeleted(invoiceType, false);

        if (invoiceType.getValue().equals("Purchase")) {

            return invoicesList.stream().map(invoice -> {
                InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
                invoiceDto.setPrice(invoicePrice(invoiceDto));
                invoiceDto.setTax(invoiceTax(invoiceDto));
                invoiceDto.setTotal(invoiceTotalPrice(invoiceDto));

                return invoiceDto;
            }).sorted(Comparator.comparing(InvoiceDto::getInvoiceNo).reversed()).collect(Collectors.toList());
        } else if (invoiceType.getValue().equals("Sales")){
            return invoicesList.stream().map(invoice -> {
                InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());
                invoiceDto.setPrice(invoicePrice(invoiceDto));
                invoiceDto.setTax(invoiceTax(invoiceDto));
                invoiceDto.setTotal(invoiceTotalPrice(invoiceDto));

                return invoiceDto;
            }).collect(Collectors.toList());
        }
        return null;
    }


    @Override
    public BigDecimal invoicePrice(InvoiceDto invoiceDto) { // Sum of the Invoice Product price
        return invoiceDto.getInvoiceProducts().stream()
                .map(InvoiceProductDto::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    @Override
    public Integer invoiceTax(InvoiceDto invoiceDto) { // Sum of the tax of the Invoice Product
        return invoiceDto.getInvoiceProducts().stream()
                .map(InvoiceProductDto::getTax)
                .reduce(0, Integer::sum);
    }

    @Override
    public BigDecimal invoiceTotalPrice(InvoiceDto invoiceDto) { // Invoice tax+ Invoice price
        BigDecimal price = invoicePrice(invoiceDto);
        Integer tax = invoiceTax(invoiceDto);
        return  price.add(BigDecimal.valueOf(tax));
    }
    @Override
    public void updateInvoice(InvoiceDto invoiceDto) {

        Invoice updatedInvoice = mapperUtil.convert(invoiceDto, new Invoice());
        Invoice invoice = invoiceRepository.findByIdAndIsDeleted(invoiceDto.getId(), false);
        invoice.setCompany(updatedInvoice.getCompany());
        invoiceRepository.save(invoice);

    }

    @Override
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findByIdAndIsDeleted(id, false);
        if (invoice.getInvoiceStatus().getValue().equals("Awaiting Approval")) {
            invoice.setIsDeleted(true);
            invoiceRepository.save(invoice);
        }
    }

    @Override
    public void approveInvoice(Long id) {
        Invoice invoice = invoiceRepository.findByIdAndIsDeleted(id, false);
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        //Change product quantities

       // productService.update(invoice.getInvoiceProduct());  to update stock values?
        invoiceRepository.save(invoice);
    }

    @Override
    public InvoiceDto create(InvoiceDto invoiceDto) {

        User user = mapperUtil.convert(securityService.getLoggedInUser(), new User());

        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());
        invoice.setCompany(user.getCompany());
        invoiceRepository.save(invoice);
        return invoiceDto;
    }

    @Override
    public InvoiceDto findInvoiceById(long id) {
        Invoice invoice = invoiceRepository.findByIdAndIsDeleted(id, false);
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public InvoiceDto createNewPurchaseInvoiceDto() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo("P-" + InvoiceNo(InvoiceType.PURCHASE));
        invoice.setDate(LocalDate.now());
        invoice.setInvoiceType(InvoiceType.PURCHASE);
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public InvoiceDto createNewSalesInvoiceDto() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo("S-" + InvoiceNo(InvoiceType.SALES));
        invoice.setDate(LocalDate.now());
        invoice.setInvoiceType(InvoiceType.SALES);
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    public String InvoiceNo(InvoiceType invoiceType){
       Long id= invoiceRepository.getMaxId(invoiceType);
       String InvoiceNo=String.format("%03d",id+1 );
       return InvoiceNo;
    }
}
