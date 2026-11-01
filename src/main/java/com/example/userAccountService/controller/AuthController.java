package com.example.userAccountService.controller;


import com.example.userAccountService.dto.ApiResponse;
import com.example.userAccountService.dto.AuthResponse;
import com.example.userAccountService.dto.LoginRequest;
import com.example.userAccountService.dto.RegistrationRequest;
import com.example.userAccountService.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody @Valid RegistrationRequest registrationRequest) {
       return new ResponseEntity<>(authService.registerUser(registrationRequest),HttpStatus.OK );
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.loginUser(loginRequest),HttpStatus.OK );
    }


}
