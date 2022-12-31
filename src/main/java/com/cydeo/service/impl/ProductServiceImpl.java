package com.cydeo.service.impl;

import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Product;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MapperUtil mapperUtil;

    public ProductServiceImpl(ProductRepository productRepository, MapperUtil mapperUtil) {
        this.productRepository = productRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public ProductDto findProductById(Long productId) {
     Product product = productRepository.findById(productId).get();

        return mapperUtil.convert(product, new ProductDto());
    }

    @Override
    public List<ProductDto> getAllProducts() {
        List<Product> productList = productRepository.findAll();
        return productList.stream().map(product -> mapperUtil.convert(product, new ProductDto())).collect(Collectors.toList());
    }


    @Override
    public ProductDto save(ProductDto productDto) {
      productRepository.save(mapperUtil.convert(productDto, new Product()));
        return productDto;
    }

    @Override
    public ProductDto update(Long productId, ProductDto productDto) {
       Product product = productRepository.findById(productId).get();
        return null;
    }

    @Override
    public void delete(Long productId) {
        Product product = productRepository.findById(productId).get();
        product.setIsDeleted(true);
        productRepository.save(product);
    }

    @Override
    public List<ProductDto> findAllProductsWithCategoryId(Long categoryId) {
        List<Product> productsList = productRepository.findAllByCategoryId(categoryId);
        return productsList.stream().map(product -> mapperUtil.convert(product, new ProductDto())).collect(Collectors.toList());
    }

    @Override
    public boolean isProductNameExist(ProductDto productDto) {
        return productRepository.existsByName(productDto.getName());
    }
}
