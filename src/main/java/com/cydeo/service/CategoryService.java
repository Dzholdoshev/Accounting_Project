package com.cydeo.service;

import com.cydeo.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto findCategoryById(Long id);
    List<CategoryDto> listAllCategories();

    void deleteCategoryById(Long id);
}
