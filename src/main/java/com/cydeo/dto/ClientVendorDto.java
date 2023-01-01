package com.cydeo.dto;

import com.cydeo.enums.ClientVendorType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientVendorDto {

  private Long id;
  @NotBlank(message = "Company Name is a required field.")
  @Size(max = 50, min = 2, message = "Company Name should have 2-50 characters long.")
  private String clientVendorName;
  @NotBlank
  @Pattern(regexp = "^\\s?((\\+[1-9]{1,4}[ \\-]*)|(\\([0-9]{2,3}\\)[ \\-]*)|([0-9]{2,4})[ \\-]*)*?[0-9]{3,4}?[ \\-]*[0-9]{3,4}?\\s?"
          , message = "Phone is required field and may be in any valid phone number format." )
  private String phone;
  @NotBlank()
  @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",message = "Website should have a valid format.")
  private String website;
  @NotNull(message = "Please select type.")
  private ClientVendorType clientVendorType;
  //@NotBlank(message = "Address is a required field.")
  //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private AddressDto address;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private CompanyDto company;

  public ClientVendorDto(String clientVendorName, String phone, String website, ClientVendorType clientVendorType, AddressDto address, CompanyDto company) {
    this.clientVendorName = clientVendorName;
    this.phone = phone;
    this.website = website;
    this.clientVendorType = clientVendorType;
    this.address = address;
    this.company = company;
  }
}
