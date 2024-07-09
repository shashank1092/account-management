package com.supplyhouse.account_management.entity;

import com.supplyhouse.account_management.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "ACCOUNT")
public class UserAccount {

    @Column(name = "ACCOUNT_ID",nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(name = "USER_NAME", nullable = false)
    private String userName;

    @Column(name = "PASSWORD",nullable = false)
    private String password;

    @Column(name = "ACCOUNT_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "EMAIL_ID",nullable = false)
    private String emailId;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Order> orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ACCOUNT_ID")
    private UserAccount parentAccount;

    @Column(name = "CREATION_DATE",nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column(name = "SHARE_HISTORICAL_ORDERS")
    private Boolean shareHistoricalOrders;

    @Column(name = "INITIAL_DATE_TO_SHARE")
    private LocalDateTime initialDateToShare;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccount account = (UserAccount) o;
        return Objects.equals(accountId, account.accountId) && Objects.equals(userName, account.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, userName);
    }
}
