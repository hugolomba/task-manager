package com.hugo.taskmanager.service;

import com.hugo.taskmanager.dto.UserRequest;
import com.hugo.taskmanager.dto.UserResponse;
import com.hugo.taskmanager.entity.User;
import com.hugo.taskmanager.exception.UsernameAlreadyExistsException;
import com.hugo.taskmanager.mapper.UserMapper;
import com.hugo.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createsUserWithEncodedPassword() {
        UserRequest request = new UserRequest("Hugo", "Lomba", "plain-password", "hugo");
        User user = User.builder()
                .name("Hugo")
                .surname("Lomba")
                .password("plain-password")
                .username("hugo")
                .build();
        User savedUser = User.builder()
                .id(1L)
                .name("Hugo")
                .surname("Lomba")
                .password("encoded-password")
                .username("hugo")
                .build();
        UserResponse response = new UserResponse(1L, "Hugo", "Lomba", "hugo", null);

        when(userRepository.existsByUsername("hugo")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(encoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(response);

        UserResponse result = userService.createUser(request);

        assertSame(response, result);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded-password", userCaptor.getValue().getPassword());
    }

    @Test
    void rejectsDuplicateUsername() {
        UserRequest request = new UserRequest("Hugo", "Lomba", "plain-password", "hugo");
        when(userRepository.existsByUsername("hugo")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(request));
    }
}
