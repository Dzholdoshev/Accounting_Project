package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.entity.Product;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.NotEnoughProductException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {

    private final InvoiceProductRepository invoiceProductRepository;
    private final InvoiceService invoiceService;
    private final MapperUtil mapperUtil;
    private final ProductService productService;


    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, @Lazy InvoiceService invoiceService, MapperUtil mapperUtil, ProductService productService) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.invoiceService = invoiceService;
        this.mapperUtil = mapperUtil;
        this.productService = productService;
    }

    @Override
    public InvoiceProductDto findInvoiceProductById(long id) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(id).orElseThrow();
        return mapperUtil.convert(invoiceProduct, new InvoiceProductDto());
    }


    @Override
    public List<InvoiceProductDto> getInvoiceProductsOfInvoice(Long invoiceId) {
        List<InvoiceProduct> list = invoiceProductRepository.findAllByInvoice_Id(invoiceId);
       return list.stream().map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .map(invoiceProductDto -> {
                    BigDecimal price = invoiceProductDto.getPrice();
                    BigDecimal tax = BigDecimal.valueOf(invoiceProductDto.getTax()).divide(BigDecimal.valueOf(100));
                    BigDecimal totalPriceWithOutTax = price.multiply(BigDecimal.valueOf(invoiceProductDto.getQuantity()));
                    BigDecimal totalWithTax = totalPriceWithOutTax.multiply(tax).add(totalPriceWithOutTax).setScale(2, RoundingMode.HALF_UP);
                    invoiceProductDto.setTotal(totalWithTax);
                    return invoiceProductDto;
                }).collect(Collectors.toList());
}

    @Override
    public void save(Long invoiceId, InvoiceProductDto invoiceProductDto) {
        InvoiceDto invoiceDto = mapperUtil.convert(invoiceService.findInvoiceById(invoiceId), new InvoiceDto());
        invoiceProductDto.setInvoice(invoiceDto);
        //remaining quantity
        //profitLoss
        //price
        //tax

        invoiceProductRepository.save(mapperUtil.convert(invoiceProductDto, new InvoiceProduct()));
    }

    @Override
    public void delete(Long invoiceProductId) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findInvoiceProductById(invoiceProductId);
        invoiceProduct.setIsDeleted(true);
        invoiceProductRepository.save(invoiceProduct);
    }

    @Transactional
    @Override
    public void completeApprovalProcedures(Long invoiceId, InvoiceType type) throws NotEnoughProductException {
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAllByInvoice_Id(invoiceId);
        if (type == InvoiceType.SALES) {
            for (InvoiceProduct salesInvoiceProduct : invoiceProductList) {
                //If there is enough stock
                if (salesInvoiceProduct.getProduct().getQuantityInStock() >= salesInvoiceProduct.getQuantity()) {
                    //decrease the product quantity based on the quantity we are selling
                    updateQuantityOfProduct(salesInvoiceProduct, type);
                    //assign quantity to remainingQty (currently null), will be updated when doing profit/loss calculation
                    salesInvoiceProduct.setRemainingQuantity(salesInvoiceProduct.getQuantity());
                    //updating
                    invoiceProductRepository.save(salesInvoiceProduct);
                    //calculate profit/loss and update remaining quantity values
                    setProfitLossOfInvoiceProductsForSalesInvoice(salesInvoiceProduct);

                } else {
                    throw new NotEnoughProductException("This sale cannot be completed due to insufficient quantity of product");
                }
            }
        } else {
            for (InvoiceProduct purchaseInvoiceProduct : invoiceProductList) {
                //increase the product quantity based on the amount purchased
                updateQuantityOfProduct(purchaseInvoiceProduct, type);
                purchaseInvoiceProduct.setRemainingQuantity(purchaseInvoiceProduct.getQuantity());
                //updating
                invoiceProductRepository.save(purchaseInvoiceProduct);
            }
        }
    }


    private void setProfitLossOfInvoiceProductsForSalesInvoice(InvoiceProduct toBeSoldProduct) {

    }

    private void updateQuantityOfProduct(InvoiceProduct invoiceProduct, InvoiceType type) {
        ProductDto productDto = mapperUtil.convert(invoiceProduct.getProduct(), new ProductDto());
        if (type.equals(InvoiceType.SALES)) {// increasing quantity in stock
            productDto.setQuantityInStock(productDto.getQuantityInStock() - invoiceProduct.getQuantity());
        } else { //decrease quantity in stock
            productDto.setQuantityInStock(productDto.getQuantityInStock() + invoiceProduct.getQuantity());
        }
        //calling productService to update database
        productService.update(productDto.getId(), productDto);

    }

    @Override
    public boolean checkProductQuantity(InvoiceProductDto salesInvoiceProduct) {
        return salesInvoiceProduct.getProduct().getQuantityInStock() >= salesInvoiceProduct.getQuantity();
    }

    @Override
    public List<InvoiceProduct> findInvoiceProductsByInvoiceTypeAndProductRemainingQuantity(InvoiceType type, Product product, Integer remainingQuantity) {
        return invoiceProductRepository.findInvoiceProductsByInvoiceInvoiceTypeAndProductAndRemainingQuantityNotOrderByIdAsc(type, product, remainingQuantity);
    }

    @Override
    public List<InvoiceProductDto> findAllInvoiceProductsByProductId(Long id) {
        return invoiceProductRepository.findAllInvoiceProductByProductId(id).stream()
                .map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .collect(Collectors.toList());
    }

}