package com.example.userAccountService.service.impl;

import com.example.userAccountService.dto.*;
import com.example.userAccountService.entity.Account;
import com.example.userAccountService.entity.Role;
import com.example.userAccountService.entity.User;
import com.example.userAccountService.enums.AccountStatus;
import com.example.userAccountService.enums.AccountType;
import com.example.userAccountService.enums.Currency;
import com.example.userAccountService.exceptions.BadRequestException;
import com.example.userAccountService.exceptions.NotFoundException;
import com.example.userAccountService.kafka.dto.UserRegistrationEvent;
import com.example.userAccountService.kafka.service.AccountEventPublisher;
import com.example.userAccountService.repository.AccountRepository;
import com.example.userAccountService.repository.RoleRepository;
import com.example.userAccountService.repository.UserRepository;
import com.example.userAccountService.security.JwtService;
import com.example.userAccountService.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final AccountEventPublisher accountEventPublisher;

    @Override
    public ApiResponse<AuthResponse> registerUser(RegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.getEmail()))
            throw new BadRequestException("account already exists.");
        Set<Role> roles = new HashSet<>();
        String roleName = (registrationRequest.getRole() != null && !registrationRequest.getRole().isBlank())
                ? registrationRequest.getRole().toUpperCase()
                : "CUSTOMER";
        Role databaseRole = roleRepository.findByName(roleName).orElseThrow(() -> new NotFoundException("role not found."));
        roles.add(databaseRole);
        User userToSave = User.builder()
                .email(registrationRequest.getEmail())
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .enabled(true)
                .roles(roles)
                .build();
        User savedUser = userRepository.save(userToSave);

        String accountNumber = generateAccountNumber();
        Account accountToSave = Account.builder()
                .accountNumber(accountNumber)
                .accountStatus(AccountStatus.ACTIVE)
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .currency(Currency.USD)
                .user(savedUser)
                .build();
        accountRepository.save(accountToSave);
        UserRegistrationEvent event = new UserRegistrationEvent();
        event.setEmail(savedUser.getEmail());
        event.setAccountNumber(accountNumber);
        event.setFirstName(savedUser.getFirstName());
        event.setLastName(savedUser.getLastName());
        accountEventPublisher.publishedUserRegistrationEvent(event);
        //generate token for user
//        String token = jwtService.generateToken(savedUser.getEmail(),savedUser.getRoles());
        //convert to dto
        UserDTO userDTO = modelMapper.map(savedUser, UserDTO.class);

        AuthResponse authResponse = AuthResponse.builder()
                .user(userDTO)
//                .token(token)
                .build();

        return new ApiResponse<AuthResponse>(HttpStatus.CONTINUE.value(), "user created successfully", authResponse);
    }

    @Override
    public ApiResponse<AuthResponse> loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new NotFoundException("user not found."));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
            throw new BadRequestException("user not found");
        if (!user.isEnabled()) throw new BadRequestException("user is disabled contact support");

        List<String> roles =user.getRoles().stream().map(Role::getName).toList();
        String token = jwtService.generateToken(user.getEmail(),roles);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build();
        return new ApiResponse<AuthResponse>(HttpStatus.OK.value(), "user logged in successfully", authResponse);
    }


    private String generateAccountNumber() {
        String accountNumber;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        do {
            int randomPart = random.nextInt(100_000_000);
            accountNumber = String.format("00%08d", randomPart);
        }
        while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
