package com.cydeo.repository;

import com.cydeo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserById(Long id);
    User findByUsername(String username);
    List<User> findAllByRole_Description(String roleDescription);
    List<User> findAllByCompany_Title(String companyName);
    List<User> findAllByCompany_TitleAndRole_Description(String companyTitle, String role);
}
