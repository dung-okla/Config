package com.rs.cancel.reponsitory;

import com.rs.cancel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserReponsitory extends JpaRepository<User, Long> {
  Optional<User>  findByUsernameOrEmail(String username, String email);
}
