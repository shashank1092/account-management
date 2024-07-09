package com.supplyhouse.account_management.dto;

import com.supplyhouse.account_management.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseOrderDto {

    private Long orderId;

    private Double totalCost;

    private Long accountId;

    private LocalDateTime orderDate;

    private OrderStatus orderStatus;
}
