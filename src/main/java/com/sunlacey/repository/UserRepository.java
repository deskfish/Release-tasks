package com.sunlacey.repository;

import com.sunlacey.eneity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findByActiveTrue();
    boolean existsByUsername(String username);
}