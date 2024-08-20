package com.rs.cancel.reponsitory;

import com.rs.cancel.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ProfileReponsitory extends JpaRepository<Profile, Long> {
}
