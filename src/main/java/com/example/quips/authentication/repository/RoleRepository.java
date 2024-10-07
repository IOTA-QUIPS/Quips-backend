package com.example.quips.authentication.repository;

import com.example.quips.authentication.domain.model.ERole;
import com.example.quips.authentication.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
