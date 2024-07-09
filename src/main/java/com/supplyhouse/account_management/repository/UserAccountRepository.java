package com.supplyhouse.account_management.repository;

import com.supplyhouse.account_management.entity.UserAccount;
import com.supplyhouse.account_management.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUserName(String userName);

    Optional<UserAccount> findByAccountId(Long accountId);

    List<UserAccount> findByAccountType(AccountType accountType);

    List<UserAccount> findByAccountTypeAndParentAccount(AccountType accountType, UserAccount account);

}
