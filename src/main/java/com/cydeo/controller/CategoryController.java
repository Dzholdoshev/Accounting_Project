package com.cydeo.controller;

import com.cydeo.dto.CategoryDto;
import com.cydeo.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    CategoryService categoryService;

    @GetMapping("/list")
    public  String listAllCategories(Model model){

        List<CategoryDto> categoryDtoList = categoryService.listAllCategories();
        model.addAttribute("categories",categoryDtoList);

        return "category/category-list";
    }

    @GetMapping("/update/{id}")// th:href="@{/categories/update/{id}(id=${category.getId()})}"
    public String editCategory(@PathVariable("id") Long id, Model model){

        model.addAttribute("categories",categoryService.findCategoryById(id));

        return "category/category-update";

    }

  //  Edit each category, when click on Edit button, end-user should land on category_update page
    //  and the edit form should be populated with the information of that very same category.

  @GetMapping("/delete/{id}")//   th:href="@{/categories/delete/{id}(id=${category.getId()})}"
  public String deleteCategory(@PathVariable("id") Long id) {

      categoryService.deleteCategoryById(id);

      return "redirect:/categories/list";
  }

    //End-user should be able to Delete each category(soft delete), then end up to the category_list
    // page with updated category list.



    //When End-User clicks on "Create-Category" button, category_create page should be displayed with
    // an Empty category form
}
