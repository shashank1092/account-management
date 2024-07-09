package com.supplyhouse.account_management.repository;

import com.supplyhouse.account_management.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("From Order o where o.account.accountId = :accountId")
    List<Order> getOrdersForIndividualAccount(Long accountId);

    @Query("From Order o where o.account.accountId = :accountId AND o.orderDate > :orderDate")
    List<Order> getOrdersForIndividualAccountAfterGivenDate(Long accountId, LocalDateTime orderDate);

    @Query("From Order o JOIN o.account ac where ac.accountId = :accountId OR ac.parentAccount.accountId = :accountId")
    List<Order> getAllOrdersLinkedToBusinessOwnerAccount(Long accountId);
}
