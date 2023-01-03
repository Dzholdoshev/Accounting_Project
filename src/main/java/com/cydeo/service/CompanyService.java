package com.cydeo.service;

import com.cydeo.dto.CompanyDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CompanyService {

    CompanyDto findCompanyById(Long id);
    //for update?

    CompanyDto findCompanyByTitle(String Title);
    //for update?

    CompanyDto getCompanyByLoggedInUser();

    List<CompanyDto> getAllCompanies();

    List<CompanyDto> getFilteredCompaniesForCurrentUser();

//    CompanyDto create(CompanyDto companyDto);

//    CompanyDto update(Long companyId, CompanyDto companyDto) throws CloneNotSupportedException;

    void activate(Long companyId);

    void deactivate(Long companyId);

    boolean isTitleExist(String title);

    void save(CompanyDto company);

    CompanyDto updateCompany(CompanyDto companyDto);
}
