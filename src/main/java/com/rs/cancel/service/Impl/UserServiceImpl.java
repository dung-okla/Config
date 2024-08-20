package com.rs.cancel.service.Impl;

import com.rs.cancel.config.JwtTokenProvider;
import com.rs.cancel.dto.LoginDto;
import com.rs.cancel.dto.ProfileDto;
import com.rs.cancel.dto.RoleDto;
import com.rs.cancel.model.Profile;
import com.rs.cancel.model.Role;
import com.rs.cancel.model.User;
import com.rs.cancel.reponsitory.ProfileReponsitory;
import com.rs.cancel.reponsitory.RoleReponsitory;
import com.rs.cancel.reponsitory.UserReponsitory;
import com.rs.cancel.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
private JwtTokenProvider jwtTokenProvider;
    private RoleReponsitory roleReponsitory;
    private PasswordEncoder passwordEncoder;
    private ProfileReponsitory profileReponsitory;
    private UserReponsitory userReponsitory;

    @Override
    public String login(LoginDto loginDto) {
        String token = jwtTokenProvider.generateToken(loginDto);
        return token;
    }

    @Override
    public User register(LoginDto loginDto) {
        Role role = roleReponsitory.findByRole("ROLE_USER").orElseGet(() -> {
            Role newRole = Role.builder().role("ROLE_USER").build();
            return roleReponsitory.save(newRole);
        });
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        Profile profile =new Profile();
        profileReponsitory.save(profile);

User user = User.builder()
        .username(loginDto.getUsernameOrEmail())
        .password(passwordEncoder.encode(loginDto.getPassword()))
        .email(loginDto.getUsernameOrEmail())
        .profile(profile)
        .role(roles)
        .build();

        return userReponsitory.save(user);
    }

    @Override
    public User getUserByUsernameOrEmail(String username) {

        return userReponsitory.findByUsernameOrEmail(username, username).orElse(null);
    }

    @Override
    public User getUserMe()  {
     String username= (String) RequestContextHolder.getRequestAttributes()
                .getAttribute("username", RequestAttributes.SCOPE_REQUEST);
    long id = (long) RequestContextHolder.getRequestAttributes().getAttribute("userId",RequestAttributes.SCOPE_REQUEST);
    System.out.println("UserId "+id);
        String requestUrl= ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getRequestURL().toString();
        String method = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getMethod().toString();
        System.out.println("Request URl: " + requestUrl);
        System.out.println("Request Method: " + method);
return getUserByUsernameOrEmail(username);
    }

    @Override
    public User eidtMe(ProfileDto profileDto) {
        String username= (String) RequestContextHolder.getRequestAttributes()
                .getAttribute("username", RequestAttributes.SCOPE_REQUEST);
        User user = getUserByUsernameOrEmail(username);
        Profile profile = user.getProfile();
        profile.setCity(profileDto.getCity());
        profile.setGender(profileDto.getGender());
        profileReponsitory.save(profile);
        user.setProfile(profile);
        return userReponsitory.save(user);
    }

    @Override
    public User editRole(long id, RoleDto roleDto) {
        User user=   userReponsitory.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        Role role = roleReponsitory.findByRole(roleDto.getRole()).orElseGet(() -> {
            Role newRole = Role.builder().role(roleDto.getRole()).build();
            return roleReponsitory.save(newRole);
        });


        user.getRole().add(role);

        return userReponsitory.save(user);
    }

    @Override
    public List<User> listUser() {
        return userReponsitory.findAll();
    }


}
