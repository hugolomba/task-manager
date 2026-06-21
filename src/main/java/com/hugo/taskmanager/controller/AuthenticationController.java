package com.hugo.taskmanager.controller;

import com.hugo.taskmanager.dto.UserRequest;
import com.hugo.taskmanager.dto.UserResponse;
import com.hugo.taskmanager.entity.User;
import com.hugo.taskmanager.mapper.UserMapper;
import com.hugo.taskmanager.repository.UserRepository;
import com.hugo.taskmanager.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtils;
    private final UserMapper userMapper;

    public AuthenticationController(AuthenticationManager authenticationManager,UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtils, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.encoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
    }

    @PostMapping("/signin")
    public String authenticateUser(@RequestBody UserRequest userRequest) {

        User userEntity = userMapper.toEntity(userRequest);

        Authentication authentication = authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                      userEntity.getUsername(),
                      userEntity.getPassword()
                )
        );

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtils.generateToken(userDetails.getUsername());
    }

    @PostMapping("signup")
    public UserResponse registerUser(@RequestBody UserRequest userRequest) {
//        if (userRepository.existsByUsername(userRequest.getUsername()) {
//            return "User already exists!";
//        }

        final User newUser = userMapper.toEntity(userRequest);

        userRepository.save(newUser);
        return userMapper.toResponse(newUser);
    }
}
