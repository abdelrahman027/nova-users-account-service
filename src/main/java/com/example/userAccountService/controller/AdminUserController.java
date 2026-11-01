package com.example.userAccountService.controller;


import com.example.userAccountService.dto.ApiResponse;
import com.example.userAccountService.dto.UserDTO;
import com.example.userAccountService.dto.UserStatisticsDTO;
import com.example.userAccountService.dto.UserWithAccountDTO;
import com.example.userAccountService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserWithAccountDTO>> search(@RequestParam(required = false) String email , @RequestParam(required = false) String accountNumber) {
        return new ResponseEntity<>(userService.searchUser(email,accountNumber), HttpStatus.OK);
    }


    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(@RequestParam(required = false) String roleName, @PageableDefault(page = 0,size = 100) Pageable pageable) {
        return new ResponseEntity<>(userService.getAllUsers(roleName,pageable), HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<UserStatisticsDTO>> getUserStats() {
        return new ResponseEntity<>(userService.getUserStatistics(), HttpStatus.OK);
    }


    @PatchMapping("/toggle-status/{userId}")
    public ResponseEntity<ApiResponse<String>> toggleUserStatus(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.toggleUserStatus(userId), HttpStatus.OK);
    }


}
