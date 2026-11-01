package com.example.userAccountService.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStatisticsDTO {
    private long totalUsers;
    private long activeUsers;
    private long inActiveUsers;
    private long totalAccounts;

    private long averageAccountPerUser;

    private long customersCount;
    private long adminsCount;


}
