package com.hugo.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
    @NotBlank(message = "Name is mandatory")
    @Size(min = 3, max = 100, message = "Surname must be between 3 and 100 characters")
    String name,

    @NotBlank(message = "Surname is mandatory")
    @Size(min = 3, max = 100, message = "Surname must be between 3 and 100 characters")
    String surname,

    @NotBlank(message = "Password is mandatory")
    @Size(min = 3, max = 100, message = "Password must be between 3 and 100 characters")
    String password,

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 20 characters")
    String username

) {
}
