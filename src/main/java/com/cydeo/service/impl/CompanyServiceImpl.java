package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.Company;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;

    public CompanyServiceImpl(CompanyRepository companyRepository, MapperUtil mapperUtil, SecurityService securityService) {
        this.companyRepository = companyRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
    }

    @Override
    public CompanyDto findCompanyById(Long id){

        Company company = companyRepository.findById(id).get();

        return mapperUtil.convert(company,new CompanyDto());
    }

    @Override
    public CompanyDto findCompanyByTitle(String title) {

        Company company = companyRepository.findCompanyByTitle(title);

        return mapperUtil.convert(company,new CompanyDto());
    }

    @Override
    public CompanyDto getCompanyByLoggedInUser() {

        UserDto loggedInUser = securityService.getLoggedInUser();

        return null;

    }

    @Override
    public List<CompanyDto> getAllCompanies() {

        return companyRepository.findAll()
                .stream()
                .map(company -> mapperUtil.convert(company,new CompanyDto()))
                .collect(Collectors.toList());

    }

    @Override
    public List<CompanyDto> getFilteredCompaniesForCurrentUser() {

        UserDto loggedInUser = securityService.getLoggedInUser();

        return null;
    }

    @Override
    public CompanyDto create(CompanyDto companyDto) {
        return null;
    }

    @Override
    public CompanyDto update(Long companyId, CompanyDto companyDto) throws CloneNotSupportedException {
        return null;
    }

    @Override
    public void activate(Long companyId) {

        Company company = companyRepository.findById(companyId).orElseThrow(); //future exception message?
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        companyRepository.save(company);

    }

    @Override
    public void deactivate(Long companyId) {

        Company company = companyRepository.findById(companyId).orElseThrow(); //future exception message?
        company.setCompanyStatus(CompanyStatus.PASSIVE);
        companyRepository.save(company);

    }

    @Override
    public boolean isTitleExist(String title) {
        return false;
    }

}
