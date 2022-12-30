package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.Company;
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

        UserDto user = securityService.getLoggedInUser();
        return user.getCompany();

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

    }

    @Override
    public void deactivate(Long companyId) {

    }

    @Override
    public boolean isTitleExist(String title) {
        return false;
    }

}
