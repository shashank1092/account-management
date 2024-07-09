package com.supplyhouse.account_management.dto;

import com.supplyhouse.account_management.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAccountDto {

    @NotBlank(message = "User name is mandatory")
    private String userName;

    @NotBlank(message = "Password is mandatory")
    private String password;

    private AccountType accountType;

    @NotBlank(message = "Email Id is mandatory")
    private String emailId;

}
