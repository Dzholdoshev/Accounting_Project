package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
//@Where(clause = "is_deleted=false")
@Table(name="invoice_products")
public class InvoiceProduct extends BaseEntity {
    private BigDecimal price;
    private BigDecimal profitLoss;
    @NotNull(message = "Quantity is a required field.")
    @Range(min = 1, max = 100, message = "Maximum order count is 100")
    private int quantity;
    private int remainingQuantity;
    private int tax;
    @ManyToOne(fetch= FetchType.LAZY)
    private Invoice invoice;
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
}
