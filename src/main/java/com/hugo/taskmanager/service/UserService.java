package com.hugo.taskmanager.service;

import com.hugo.taskmanager.dto.UserRequest;
import com.hugo.taskmanager.dto.UserResponse;
import com.hugo.taskmanager.entity.User;
import com.hugo.taskmanager.mapper.UserMapper;
import com.hugo.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    // Create user
    public UserResponse createUser(UserRequest user) {

        User userEntity = userMapper.toEntity(user);
        User savedEntity = userRepository.save(userEntity);

        return userMapper.toResponse(savedEntity);

    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Find user by id
    public UserResponse getUserById(Long id) {
        User retrievedUser = userRepository.findById(id).orElseThrow();

        return userMapper.toResponse(retrievedUser);
    }
}
