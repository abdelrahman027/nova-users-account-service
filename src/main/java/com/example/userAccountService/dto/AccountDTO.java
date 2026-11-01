package com.example.userAccountService.dto;


import com.example.userAccountService.entity.User;
import com.example.userAccountService.enums.AccountStatus;
import com.example.userAccountService.enums.AccountType;
import com.example.userAccountService.enums.Currency;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO {

    private long id;


    private String accountNumber;

    private BigDecimal balance;

    private Currency currency;

    private AccountType accountType;


    private AccountStatus accountStatus;


//    private UserDTO user;
    private String ownerEmail;
    private  LocalDateTime createdAt;
}
