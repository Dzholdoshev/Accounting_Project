package com.cydeo.dto;

import com.cydeo.enums.ClientVendorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientVendorDto {

  private Long id;
  private String clientVendorName;
  private String phone;
  private String website;
  private ClientVendorType clientVendorType;
  private AddressDto address;
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
