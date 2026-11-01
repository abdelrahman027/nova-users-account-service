package com.example.userAccountService.repository;

import com.example.userAccountService.entity.Role;
import com.example.userAccountService.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByEnabledTrue();

    List<User> findByEnabledTrue();

    @Query("select  count(distinct u) from User u join u.roles r where r.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

    @Query("select u from User u join u.roles r where r =:role")
    List<User> findByRolesContaining(@Param("role") Role role);


    @Query("select u from User u join u.roles r where r.name =:roleName")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);
}
