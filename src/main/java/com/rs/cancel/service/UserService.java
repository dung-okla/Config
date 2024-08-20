package com.rs.cancel.service;

import com.rs.cancel.dto.LoginDto;
import com.rs.cancel.dto.ProfileDto;
import com.rs.cancel.dto.RoleDto;
import com.rs.cancel.model.User;

import java.util.List;

public interface UserService {
    String login(LoginDto loginDto);
    User register(LoginDto loginDto);
    User getUserByUsernameOrEmail(String username);

    User getUserMe() ;

    User eidtMe(ProfileDto profileDto);

    User editRole(long id, RoleDto role);

    List<User> listUser();
}
