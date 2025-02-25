package com.Realestate.RealEstateManagement.Repository.UserRepository;

import com.Realestate.RealEstateManagement.Entity.Role;
import com.Realestate.RealEstateManagement.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
}
