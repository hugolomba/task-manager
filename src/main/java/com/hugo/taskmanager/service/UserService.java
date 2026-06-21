package com.hugo.taskmanager.service;

import com.hugo.taskmanager.dto.UserRequest;
import com.hugo.taskmanager.dto.UserResponse;
import com.hugo.taskmanager.entity.User;
import com.hugo.taskmanager.mapper.UserMapper;
import com.hugo.taskmanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;


    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.encoder = encoder;
    }

    // Create/Register user
    public UserResponse createUser(UserRequest user) {

        User userEntity = userMapper.toEntity(user);
        userEntity.setPassword(encoder.encode(userEntity.getPassword()));
        User savedEntity = userRepository.save(userEntity);

        return userMapper.toResponse(savedEntity);

    }

    // Get all users
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    // Find user by id
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
}
