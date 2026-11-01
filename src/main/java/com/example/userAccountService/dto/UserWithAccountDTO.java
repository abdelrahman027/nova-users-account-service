package com.example.userAccountService.dto;

import com.example.userAccountService.entity.User;
import com.example.userAccountService.enums.AccountStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserWithAccountDTO {
    private UserDTO user;
    private AccountDTO account;

}
