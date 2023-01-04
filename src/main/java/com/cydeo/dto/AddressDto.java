package com.cydeo.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

    private Long id;
    @NotBlank(message = "Address can't be empty")
    private String addressLine1;
    @NotBlank(message = "Address can't be empty")
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    @NotBlank(message = "Zip Code is required")
    @Size(min = 5, max = 5)
    private String zipCode;


}
