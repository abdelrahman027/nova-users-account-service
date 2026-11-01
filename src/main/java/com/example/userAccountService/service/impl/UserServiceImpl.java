package com.example.userAccountService.service.impl;

import com.example.userAccountService.dto.*;
import com.example.userAccountService.entity.Account;
import com.example.userAccountService.entity.User;
import com.example.userAccountService.exceptions.BadRequestException;
import com.example.userAccountService.exceptions.NotFoundException;
import com.example.userAccountService.repository.AccountRepository;
import com.example.userAccountService.repository.UserRepository;
import com.example.userAccountService.service.UserService;
import com.example.userAccountService.utils.AppHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final AppHelper appHelper;


    @Override
    public ApiResponse<UserWithAccountDTO> getMyDetails() {
       User loggedUser = userRepository.findByEmail(appHelper.getLoggedUserEmail())
               .orElseThrow(()->new NotFoundException("user not found!"));
       Account userAccount = accountRepository.findByUser(loggedUser)
               .orElseThrow(()->new NotFoundException("account not found!"));

        AccountDTO accountDTO = modelMapper.map(userAccount,AccountDTO.class);
        UserDTO userDTO = modelMapper.map(loggedUser,UserDTO.class);
        UserWithAccountDTO userWithAccountDTO = UserWithAccountDTO.builder()
                .account(accountDTO)
                .user(userDTO)
                .build();
        return new ApiResponse<>(HttpStatus.OK.value(),"user retrieved successfully" ,userWithAccountDTO);
    }

    @Override
    public ApiResponse<UserWithAccountDTO> searchUser(String email, String accountNumber) {
        User user;
        Account account;
        if (email != null && !email.isBlank()) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(()->new NotFoundException("user not found"));
            account = accountRepository.findByUser(user)
                    .orElseThrow(()->new NotFoundException("account not found"));
        } else if (accountNumber !=null && !accountNumber.isBlank()) {
            account = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(()->new NotFoundException("account not found"));
            user = account.getUser();
        } else {
            throw new BadRequestException("email or account number is required");
        }
        AccountDTO accountDTO = modelMapper.map(account,AccountDTO.class);
        UserDTO userDTO = modelMapper.map(user,UserDTO.class);
        UserWithAccountDTO userWithAccountDTO = UserWithAccountDTO.builder()
                .account(accountDTO)
                .user(userDTO)
                .build();
        return new ApiResponse<>(HttpStatus.OK.value(), "user and count retrieved successfully",userWithAccountDTO);
    }

    @Override
    public ApiResponse<Page<UserDTO>> getAllUsers(String roleName, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );
        Page<User> userPage;
        if (roleName != null && !roleName.isBlank()) {
            userPage = userRepository.findByRoleName(roleName,sortedPageable);
        } else {
            userPage = userRepository.findAll(sortedPageable);
        }

        Page<UserDTO> userDTOPage = userPage.map(user->modelMapper.map(user,UserDTO.class));

        return new ApiResponse<>(HttpStatus.OK.value(),"data retrieved successfully",userDTOPage);
    }

    @Override
    public ApiResponse<UserStatisticsDTO> getUserStatistics() {
        long total = userRepository.count();
        long enabled = userRepository.countByEnabledTrue();
        UserStatisticsDTO userStatisticsDTO = UserStatisticsDTO.builder()
                .totalUsers(total)
                .activeUsers(enabled)
                .inActiveUsers(total-enabled)
                .totalAccounts(accountRepository.count())
                .averageAccountPerUser(accountRepository.count())
                .customersCount(userRepository.countByRoleName("CUSTOMER"))
                .adminsCount(userRepository.countByRoleName("ADMIN"))
                .build();
        return new ApiResponse<>(HttpStatus.OK.value(), "data retrieved successfully",userStatisticsDTO);
    }

    @Override
    public ApiResponse<String> toggleUserStatus(Long userId) {
        User user  = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("user not found!"));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        String status = user.isEnabled()? "enabled":"disabled";
        return new ApiResponse<>(HttpStatus.OK.value(), "user is " + status +" successfully","user is "+ status + " successfully");
    }


}
