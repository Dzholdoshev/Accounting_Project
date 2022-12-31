package com.cydeo.service.impl;

import com.cydeo.dto.CategoryDto;
import com.cydeo.entity.Category;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.service.CategoryService;
import org.springframework.stereotype.Service;

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

    public List<CategoryDto> getAllCategories() throws Exception {

        return categoryRepository.findAll()
               .stream()
                .map(category -> mapperUtil.convert(category, new CategoryDto()))
               .collect(Collectors.toList());
    }

    @Override
    public void delete(Long categoryId) {
         Category category = categoryRepository.getReferenceById(categoryId);

         category.setIsDeleted(true);

         categoryRepository.save(category);

    }

    @Override
    public CategoryDto findCategoryById(Long id) {
       Category category = categoryRepository.findById(id).get();
        return mapperUtil.convert(category, new CategoryDto());
    }

    @Override
    public CategoryDto create(CategoryDto categoryDto) throws Exception {
        Category category = mapperUtil.convert(categoryDto, new Category());
       return mapperUtil.convert( categoryRepository.save(category),new CategoryDto());

    }

    @Override
    public CategoryDto update(Long categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.getReferenceById(categoryId);
        category.setDescription(categoryDto.getDescription());
        return mapperUtil.convert(categoryRepository.save(category), new CategoryDto());
    }

    @Override
    public boolean hasProduct(Long categoryId) {
        Category category = categoryRepository.getReferenceById(categoryId);
     CategoryDto categoryDto =  mapperUtil.convert(category,new CategoryDto() );
       return categoryDto.isHasProduct();
    }

    @Override
    public boolean isCategoryDescriptionExist(CategoryDto categoryDto) {
      Category category = mapperUtil.convert(categoryDto,new Category());
     return Boolean.parseBoolean(category.getDescription());

    }
}
