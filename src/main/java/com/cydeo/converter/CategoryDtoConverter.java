package com.cydeo.converter;

import com.cydeo.dto.CategoryDto;
import com.cydeo.service.CategoryService;
import com.cydeo.service.UserService;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class CategoryDtoConverter implements Converter<String, CategoryDto> {

    CategoryService categoryService;


    @Override
    public CategoryDto convert(String source) {
        if (source == null || source.equals("")){
            return  null;
        }
        return categoryService.findCategoryById(Long.parseLong(source));
    }
}
