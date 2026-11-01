package com.example.userAccountService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    @NotNull(message = "email is required")
    private String email;

    @NotNull(message = "password is required")
    private String password;

    @NotNull(message = "firstname is required")
    private String firstName;

    private String lastName;

    private String role;
}
