package com.example.userAccountService.service.impl;


import com.example.userAccountService.dto.AccountDTO;
import com.example.userAccountService.dto.ApiResponse;
import com.example.userAccountService.entity.Account;
import com.example.userAccountService.entity.User;
import com.example.userAccountService.enums.AccountStatus;
import com.example.userAccountService.exceptions.NotFoundException;
import com.example.userAccountService.repository.AccountRepository;
import com.example.userAccountService.repository.UserRepository;
import com.example.userAccountService.service.AccountService;
import com.example.userAccountService.utils.AppHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AppHelper appHelper;

    @Override
    public ApiResponse<AccountDTO> getMyAccount() {
        User loggedUser = userRepository.findByEmail(appHelper.getLoggedUserEmail())
                .orElseThrow(()->new NotFoundException("user not found"));
        Account account = accountRepository.findByUser(loggedUser)
                .orElseThrow(()->new NotFoundException("account not found"));
        AccountDTO accountDTO = modelMapper.map(account,AccountDTO.class);
        accountDTO.setOwnerEmail(loggedUser.getEmail());

        return new ApiResponse<>(HttpStatus.OK.value(), "data retrieved successfully",accountDTO);
    }

    @Override
    public ApiResponse<AccountDTO> getByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()->new NotFoundException("account not found"));
        AccountDTO accountDTO = modelMapper.map(account,AccountDTO.class);
        accountDTO.setOwnerEmail(account.getUser().getEmail());
        return new ApiResponse<>(HttpStatus.OK.value(), "data retrieved successfully",accountDTO);

    }

    @Override
    public ApiResponse<AccountDTO> changeAccountStatus(String accountNumber, AccountStatus accountStatus) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()->new NotFoundException("account not found"));

        account.setAccountStatus(accountStatus);
        accountRepository.save(account);

        return new ApiResponse<>(HttpStatus.OK.value(), "account status changed successfully",modelMapper.map(account,AccountDTO.class));

    }

    @Override
    public ApiResponse<Page<AccountDTO>> getAllAccounts(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );
        Page<Account> accountPage =  accountRepository.findAll(sortedPageable);

        Page<AccountDTO> accountDTOPage = accountPage.map((account)->modelMapper.map(account,AccountDTO.class));

        return new ApiResponse<>(HttpStatus.OK.value(), "accounts retrieved successfully",accountDTOPage);

    }
}
