package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;

import com.cydeo.entity.Company;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import org.springframework.data.domain.Sort;
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

        return mapperUtil.convert(company, new CompanyDto());
    }

    @Override
    public CompanyDto findCompanyByTitle(String title) {

        Company company = companyRepository.findCompanyByTitle(title);

        return mapperUtil.convert(company,new CompanyDto());
    }

    @Override
    public CompanyDto getCompanyByLoggedInUser() {

        return securityService.getLoggedInUser().getCompany();

    }

    @Override
    public List<CompanyDto> getAllCompanies() {

        return companyRepository.findAll(Sort.by("title"))
                .stream()
                .filter(company -> company.getId() != 1) //Removes CYDEO from list
                .map(company -> mapperUtil.convert(company,new CompanyDto()))
                .collect(Collectors.toList());

    }

    @Override
    public List<CompanyDto> getFilteredCompaniesForCurrentUser() {

        if(securityService.getLoggedInUser().getRole().getId()==1L){
            return getAllCompanies(); //takes care of ROOT
        }
        if(securityService.getLoggedInUser().getRole().getId()==2L){
            Long companyId = securityService.getLoggedInUser().getCompany().getId();
            return getAllCompanies().stream()
                    .filter(companyDto -> companyDto.getId().equals(companyId))
                    .collect(Collectors.toList());
        }
        return getAllCompanies();
        //Need to test - Lorraine wants to improve on this - whatever... >:-)

    }

//    @Override
//    public CompanyDto create(CompanyDto companyDto) {
//
//       return null;
//
//    }

//    @Override
//    public CompanyDto update(Long companyId, CompanyDto companyDto) throws CloneNotSupportedException {
//        return null;
//    }


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
        return companyRepository.existsByTitle(title);
    }

    @Override
    public void save(CompanyDto companyDto) {
        if(companyDto.getCompanyStatus()==null) companyDto.setCompanyStatus(CompanyStatus.PASSIVE);
        companyRepository.save(mapperUtil.convert(companyDto, new Company()));
    }

    @Override
    public CompanyDto updateCompany(CompanyDto companyDto) {

        Company company = companyRepository.findById(companyDto.getId()).get();
        Company convertedCompany = mapperUtil.convert(companyDto, new Company());
        convertedCompany.setId(company.getId());
        convertedCompany.setCompanyStatus(company.getCompanyStatus());
        companyRepository.save(convertedCompany);

        return findCompanyById(companyDto.getId());
    }

}
