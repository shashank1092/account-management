package com.supplyhouse.account_management.service;

import com.supplyhouse.account_management.dto.RequestOrderDto;
import com.supplyhouse.account_management.dto.ResponseAccountDto;
import com.supplyhouse.account_management.dto.ResponseOrderDto;
import com.supplyhouse.account_management.entity.Order;
import com.supplyhouse.account_management.entity.UserAccount;
import com.supplyhouse.account_management.enums.AccountType;
import com.supplyhouse.account_management.enums.OrderStatus;
import com.supplyhouse.account_management.mapper.OrderMapper;
import com.supplyhouse.account_management.repository.OrderRepository;
import com.supplyhouse.account_management.security.util.LoggedInUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService{

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    private final OrderRepository orderRepository;

    private final UserAccountService userAccountService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserAccountService userAccountService){
        this.orderRepository=orderRepository;
        this.userAccountService=userAccountService;
    }

    @Override
    public ResponseOrderDto createOrder(RequestOrderDto requestOrderDto) {
        Order order = orderMapper.toOrder(requestOrderDto);
        //Set the order status as Placed
        order.setOrderStatus(OrderStatus.PLACED);
        order = orderRepository.save(order);
        return orderMapper.toResponseOrderDto(order);
    }

    @Override
    public ResponseOrderDto getOrderById(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        Order order = orderOptional.orElseThrow(()-> new EntityNotFoundException("No Order found for orderId: "+orderId));
        return orderMapper.toResponseOrderDto(order);
    }

    @Override
    public ResponseOrderDto cancelOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        Order order = orderOptional.orElseThrow(()-> new EntityNotFoundException("No Order found for orderId: "+orderId));
        order.setOrderStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponseOrderDto(updatedOrder);
    }

    @Override
    public List<ResponseOrderDto> getOrdersForIndividualAccount(Long accountId) {
        List<Order> ordersForIndividualAccount = orderRepository.getOrdersForIndividualAccount(accountId);
        return orderMapper.toResponseOrderDtoList(ordersForIndividualAccount);
    }

    @Override
    public List<ResponseOrderDto> getOrdersForIndividualAccountAfterGivenDate(Long accountId, LocalDateTime orderDate) {
        List<Order> ordersForIndividualAccountAfterGivenDate = orderRepository.getOrdersForIndividualAccountAfterGivenDate(accountId,orderDate);
        return orderMapper.toResponseOrderDtoList(ordersForIndividualAccountAfterGivenDate);
    }

    @Override
    public List<Order> getAllOrdersLinkedToBusinessOwnerAccount(Long accountId) {
        return orderRepository.getAllOrdersLinkedToBusinessOwnerAccount(accountId);
    }

    @Override
    public List<ResponseOrderDto> getAllOrdersForLoggedInUser() throws AccountNotFoundException {
        List<ResponseOrderDto> responseOrderDtoList = new ArrayList<>();
        ResponseAccountDto loggedIAccountDto = LoggedInUser.get();
        if(loggedIAccountDto.getAccountType().equals(AccountType.ROLE_BUSINESS_OWNER_ACCOUNT)){
            UserAccount businessAccount = userAccountService.getAccountById(loggedIAccountDto.getAccountId());
            //Get Business owner own orders
            responseOrderDtoList = getOrdersForIndividualAccount(businessAccount.getAccountId());

            List<UserAccount> subAccounts = userAccountService.getSubAccountsForBusinessAccount(businessAccount.getAccountId());
            if(!CollectionUtils.isEmpty(subAccounts)){
                for(UserAccount subAccount : subAccounts){
                    //If sub account has accepted to share older orders
                    if(Boolean.TRUE.equals(subAccount.getShareHistoricalOrders())){
                        responseOrderDtoList.addAll(getOrdersForIndividualAccount(subAccount.getAccountId()));
                    }else{
                        responseOrderDtoList.addAll(getOrdersForIndividualAccountAfterGivenDate(subAccount.getAccountId(), subAccount.getInitialDateToShare()));
                    }

                }
            }


        }else{
            responseOrderDtoList = getOrdersForIndividualAccount(loggedIAccountDto.getAccountId());
        }

        return responseOrderDtoList;
    }
}
