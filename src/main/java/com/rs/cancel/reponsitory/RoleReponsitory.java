package com.rs.cancel.reponsitory;

import com.rs.cancel.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleReponsitory extends JpaRepository<Role, Integer> {
   Optional<Role>  findByRole(String role);
}
