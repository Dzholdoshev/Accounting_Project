package com.cydeo.converter;

import com.cydeo.dto.CategoryDto;
import com.cydeo.service.UserService;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class CategoryDtoConverter implements Converter<String, CategoryDto> {

  //  CategoryService categoryService;

//    public CategoryService(CategoryService categoryService) {
//        this.categoryService = categoryService;
//    }
    @Override
    public CategoryDto convert(String source) {
      //  if (source == null || source.equals("")){
            return  null;
        // }
        // return categoryService.findById(Long.parseLong(source));;
    }
}
