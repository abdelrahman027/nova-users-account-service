package com.example.userAccountService.service;

import com.example.userAccountService.dto.ApiResponse;
import com.example.userAccountService.dto.UserDTO;
import com.example.userAccountService.dto.UserStatisticsDTO;
import com.example.userAccountService.dto.UserWithAccountDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    ApiResponse<UserWithAccountDTO> getMyDetails();

    ApiResponse<UserWithAccountDTO> searchUser(String email , String accountNumber);

    ApiResponse<Page<UserDTO>> getAllUsers(String roleName, Pageable pageable);

    ApiResponse<UserStatisticsDTO> getUserStatistics();

    ApiResponse<String> toggleUserStatus(Long userId);


}
