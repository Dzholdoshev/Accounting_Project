package com.cydeo.repository;

import com.cydeo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   Optional<User>  findByUsername(String username);

    User findByUserNameAndIsDeleted(String username,Boolean deleted);

    List<User> findByRoleDescriptionIgnoreCaseAndIsDeleted(String description,Boolean deleted);
}
