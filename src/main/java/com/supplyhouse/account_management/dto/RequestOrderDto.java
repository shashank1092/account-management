package com.supplyhouse.account_management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestOrderDto {

    @NotNull(message = "totalCost is mandatory")
    private Double totalCost;

    @NotNull(message = "accountId is mandatory")
    private Long accountId;
}
