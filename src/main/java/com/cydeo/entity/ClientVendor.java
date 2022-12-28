package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "clients_vendors")
@Getter
@Setter
@NoArgsConstructor
public class ClientVendor extends BaseEntity {
  private String clientVendorName;
  private String phone;
  private String website;
  @Enumerated(EnumType.STRING)
  private ClientVendorType clientVendorType;
  @OneToOne
  @JoinColumn(name = "address_id")
  private Address address;
  @ManyToOne
  @JoinColumn(name = "company_id")
  private Company company;







}
