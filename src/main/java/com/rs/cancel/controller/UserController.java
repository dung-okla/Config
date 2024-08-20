package com.rs.cancel.controller;

import com.rs.cancel.config.JwtAuthenticationFilter;
import com.rs.cancel.dto.JwtAuthResponse;
import com.rs.cancel.dto.LoginDto;
import com.rs.cancel.dto.ProfileDto;
import com.rs.cancel.dto.RoleDto;
import com.rs.cancel.model.User;
import com.rs.cancel.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.Objects;

@RestController
@AllArgsConstructor
public class UserController {
@Autowired
private UserService userService;
@Autowired
private JwtAuthenticationFilter jwtAuthenticationFilter;
    @PostMapping("/login/user")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto){
        String token = userService.login(loginDto);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }

    @PostMapping("/register/user")
    public User register(@RequestBody LoginDto loginDto){
              return userService.register(loginDto);
    }

    @GetMapping("/api/v1/me")
    public User me()  {
        return userService.getUserMe();
    }

    @PutMapping("/api/v1/me/edit")
    public User meEdit(@RequestBody ProfileDto profileDto){
        return userService.eidtMe(profileDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}/more-role")
    public User helloAdmin(@PathVariable long id, RoleDto role){

        return userService.editRole(id,role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list/user")
    public List<User> listUser(){
        return userService.listUser();
    }

}
