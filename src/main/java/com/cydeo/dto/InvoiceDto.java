package com.cydeo.dto;

import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private Long id;
    private String invoiceNo;
    private InvoiceStatus invoiceStatus;
    private InvoiceType invoiceType;
    private LocalDate date;
    private CompanyDto company;
    private ClientVendorDto clientVendor;
    private BigDecimal price;                   //(only in Dto)
    private Integer tax;                       //(only in Dto)
    private BigDecimal total;                 //(only in Dto)
    private List<InvoiceProductDto> invoiceProducts;
}