package com.cydeo.dto;

import com.sun.istack.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private Long id;

    private String description;


    private CompanyDto company;

    private boolean hasProduct;

}
