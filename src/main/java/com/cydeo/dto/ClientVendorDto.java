package com.cydeo.dto;

import com.cydeo.enums.ClientVendorType;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientVendorDto {

    private Long id;
    @NotBlank(message = "Company Name is a required field.")
    @Size(max = 50, min = 2, message = "Company Name should have 2-50 characters long.")
    private String clientVendorName;
    @NotBlank
    @Pattern(regexp = "^\\d{11}$|^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", message = "Phone is required field and may be in any valid phone number format." )
    private String phone;
    @NotBlank
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",message = "Website should have a valid format.")
    private String website;
    @NotNull(message = "Please select type.")
    private ClientVendorType clientVendorType;
    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Valid
    private AddressDto address;
    //  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CompanyDto company;


}
