package com.cydeo.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceProductDto {
    private Long id;
    @NotNull(message= "Quantity is a required field.")
    private Integer quantity;
    private BigDecimal price;
    private Integer tax;
    private BigDecimal total;
    private BigDecimal profitLoss;
    private int remainingQuantity;
    private InvoiceDto invoice;
    private ProductDto product;
}
