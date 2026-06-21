package com.hugo.taskmanager.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
        Long id,
        String name,
        String surname,
        String username,
        LocalDateTime createdAt
) {
}
