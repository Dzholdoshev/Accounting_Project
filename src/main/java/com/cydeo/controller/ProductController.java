package com.cydeo.controller;

import com.cydeo.dto.ProductDto;
import com.cydeo.enums.ProductUnit;
import com.cydeo.service.CategoryService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public ProductController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }


    @GetMapping("/list")
    public String listAllProducts(Model model){
        model.addAttribute("products", productService.getAllProducts());

        return "product/product-list";
    }

    @GetMapping("/create")
    public String navigateToProductCreate(Model model) throws Exception {
        model.addAttribute("newProduct", new ProductDto());

        return "product/product-create";

    }

    @PostMapping("/create")
    public String createNewProduct(@Valid @ModelAttribute("newProduct") ProductDto productDto, BindingResult bindingResult, Model model){

        if (productService.isProductNameExist(productDto)){
            bindingResult.rejectValue("name", " ", "product name already exist");
        }

        if (bindingResult.hasErrors()){
            return "product/product-create";
        }

        productService.save(productDto);

        return "redirect:/products/list";
    }

    @GetMapping("/update/{id}")
    public String updateProduct(Model model, @PathVariable("{id}") Long id) throws Exception {

        model.addAttribute("product", productService.findProductById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("productUnits", Arrays.asList(ProductUnit.values()));

        return "redirect:/products/list";
    }

    @PostMapping("/update/{id}")
    public String update(@ModelAttribute("product") ProductDto productDto, @PathVariable("{id}") Long id){

        productService.update(id, productDto);

        return "redirect:/products/list";
    }

}
