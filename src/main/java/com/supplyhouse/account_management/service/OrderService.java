package com.supplyhouse.account_management.service;

import com.supplyhouse.account_management.dto.RequestOrderDto;
import com.supplyhouse.account_management.dto.ResponseOrderDto;
import com.supplyhouse.account_management.entity.Order;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    ResponseOrderDto createOrder(RequestOrderDto requestOrderDto);
    ResponseOrderDto getOrderById(Long orderId);
    ResponseOrderDto cancelOrder(Long orderId);
    List<ResponseOrderDto> getOrdersForIndividualAccount(Long accountId);
    List<ResponseOrderDto> getOrdersForIndividualAccountAfterGivenDate(Long accountId, LocalDateTime orderDate);
    List<Order> getAllOrdersLinkedToBusinessOwnerAccount(Long accountId);

    /**
     * This returns all orders linked to the logged in user,the result will also include any sub account orders if present
     * @return List of ResponseOrderDto
     */
    List<ResponseOrderDto> getAllOrdersForLoggedInUser() throws AccountNotFoundException;
}
