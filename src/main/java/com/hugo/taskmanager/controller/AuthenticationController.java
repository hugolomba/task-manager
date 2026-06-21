package com.hugo.taskmanager.controller;

import com.hugo.taskmanager.dto.AuthResponse;
import com.hugo.taskmanager.dto.LoginRequest;
import com.hugo.taskmanager.dto.UserRequest;
import com.hugo.taskmanager.dto.UserResponse;
import com.hugo.taskmanager.security.JwtUtil;
import com.hugo.taskmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtils;
    private final UserService userService;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtUtil jwtUtils, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                      loginRequest.username(),
                      loginRequest.password()
                )
        );

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String generatedToken = jwtUtils.generateToken(userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(generatedToken));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest userRequest) {

        UserResponse createdUser = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
