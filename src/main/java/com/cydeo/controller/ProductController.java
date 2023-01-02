package com.cydeo.controller;

import com.cydeo.dto.ProductDto;
import com.cydeo.service.CategoryService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public ProductController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @RequestMapping("/create")
    public String creatProduct(Model model, Long categoryId){
        model.addAttribute("newProduct", new ProductDto());
        model.addAttribute("categories", categoryService.findCategoryById(categoryId));


        return "/task/create";

    }


}
