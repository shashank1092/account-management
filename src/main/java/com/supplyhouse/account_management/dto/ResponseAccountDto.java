package com.supplyhouse.account_management.dto;

import com.supplyhouse.account_management.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAccountDto {

    private long accountId;

    private String userName;

    private AccountType accountType;

    private String emailId;

    private LocalDateTime creationDate;

}
