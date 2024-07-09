package com.supplyhouse.account_management.entity;

import com.supplyhouse.account_management.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ORDERS")
public class Order {

    @Column(name = "ORDER_ID",nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(name = "TOTAL_COST", nullable = false)
    private Double totalCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    private UserAccount account;

    @Column(name = "ORDER_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "ORDER_DATE",nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime orderDate;
}
