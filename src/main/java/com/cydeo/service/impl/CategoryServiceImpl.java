package com.cydeo.service.impl;

import com.cydeo.dto.CategoryDto;
import com.cydeo.entity.Category;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

   private final CategoryRepository categoryRepository;
   private final MapperUtil mapperUtil;

    public CategoryServiceImpl(CategoryRepository categoryRepository, MapperUtil mapperUtil) {
        this.categoryRepository = categoryRepository;
        this.mapperUtil = mapperUtil;
    }

    public List<CategoryDto> listAllCategories(){

     return    mapperUtil.convert(categoryRepository.findAll() , Arrays.asList(new CategoryDto()));

    }

    @Override
    public void deleteCategoryById(Long id) {


    }

    @Override
    public CategoryDto findCategoryById(Long id) {
       Category category = categoryRepository.findById(id).get();
        return mapperUtil.convert(category, new CategoryDto());
    }
}
