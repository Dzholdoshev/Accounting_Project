package com.cydeo.service;

import com.cydeo.dto.CompanyDto;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface CompanyService {

    CompanyDto findCompanyById(Long id);

    CompanyDto findCompanyByTitle(String Title);

    CompanyDto getCompanyByLoggedInUser();

    List<CompanyDto> getAllCompanies();

    List<CompanyDto> getFilteredCompaniesForCurrentUser();

    CompanyDto create(CompanyDto companyDto);

    CompanyDto update(Long companyId, CompanyDto companyDto) throws CloneNotSupportedException;

    void activate(Long companyId);

    void deactivate(Long companyId);

    boolean isTitleExist(String title);


}
