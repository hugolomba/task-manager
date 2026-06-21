package com.hugo.taskmanager.controller;

import com.hugo.taskmanager.dto.AuthResponse;
import com.hugo.taskmanager.dto.LoginRequest;
import com.hugo.taskmanager.dto.UserRequest;
import com.hugo.taskmanager.dto.UserResponse;
import com.hugo.taskmanager.security.JwtUtil;
import com.hugo.taskmanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void returnsTokenResponseAfterSignin() {
        LoginRequest request = new LoginRequest("hugo", "password");
        UserDetails principal = new User("hugo", "encoded-password", Collections.emptyList());
        UsernamePasswordAuthenticationToken expectedAuthToken =
                new UsernamePasswordAuthenticationToken("hugo", "password");

        when(authenticationManager.authenticate(expectedAuthToken)).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(jwtUtil.generateToken("hugo")).thenReturn("jwt-token");

        ResponseEntity<AuthResponse> response = authenticationController.authenticateUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().token());
    }

    @Test
    void delegatesSignupToUserService() {
        UserRequest request = new UserRequest("Hugo", "Lomba", "password", "hugo");
        UserResponse createdUser = new UserResponse(1L, "Hugo", "Lomba", "hugo", null);
        when(userService.createUser(request)).thenReturn(createdUser);

        ResponseEntity<UserResponse> response = authenticationController.registerUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdUser, response.getBody());
        verify(userService).createUser(request);
    }
}
