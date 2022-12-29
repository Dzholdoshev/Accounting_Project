package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.ClientVendorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients_vendors")
@Where(clause = "is_deleted=false")
public class ClientVendor extends BaseEntity {
  private String clientVendorName;
  private String phone;
  private String website;
  @Enumerated(EnumType.STRING)
  private ClientVendorType clientVendorType;
  @OneToOne
  @JoinColumn(name = "address_id")
  private Address address;
  @ManyToOne (fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private Company company;








}
