package com.supplyhouse.account_management.service;

import com.supplyhouse.account_management.dto.InvitationResponseDto;
import com.supplyhouse.account_management.dto.RequestAccountDto;
import com.supplyhouse.account_management.dto.ResponseAccountDto;
import com.supplyhouse.account_management.entity.Order;
import com.supplyhouse.account_management.entity.UserAccount;
import com.supplyhouse.account_management.enums.AccountType;
import com.supplyhouse.account_management.exception.DuplicateAccountException;
import com.supplyhouse.account_management.exception.OperationNotAllowedException;
import com.supplyhouse.account_management.mapper.UserAccountMapper;
import com.supplyhouse.account_management.repository.OrderRepository;
import com.supplyhouse.account_management.repository.UserAccountRepository;
import com.supplyhouse.account_management.security.util.LoggedInUser;
import jakarta.transaction.Transactional;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * UserAccountServiceImpl provides account-related services including loading user details
 * and managing user account data in the repository.
 */
@Service
@Transactional
public class UserAccountServiceImpl implements UserAccountService {

    private static final int MINIMUM_ORDER_REQUIRED_FOR_ACCOUNT_UPGRADE = 10;

    private final UserAccountMapper userAccountMapper = Mappers.getMapper(UserAccountMapper.class);

    UserAccountRepository userAccountRepository;

    OrderRepository orderRepository;

    PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    public UserAccountServiceImpl(UserAccountRepository userAccountRepository, OrderRepository orderRepository, PasswordEncoder passwordEncoder){
        this.userAccountRepository=userAccountRepository;
        this.orderRepository=orderRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public ResponseAccountDto registerAccount(RequestAccountDto requestAccountDto) {
        //Check if duplicate user name
        userAccountRepository.findByUserName(requestAccountDto.getUserName()).ifPresent( a ->{
            throw new DuplicateAccountException("An account already exists with the provided user name: " + requestAccountDto.getUserName());
        });
        UserAccount userAccount = userAccountMapper.toUserAccount(requestAccountDto);
        //Set the hashed password before saving to DB
        userAccount.setPassword(passwordEncoder.encode(requestAccountDto.getPassword()));
        userAccount.setAccountType(AccountType.ROLE_INDIVIDUAL_ACCOUNT);
        userAccount = userAccountRepository.save(userAccount);
        return userAccountMapper.toResponseAccountDto(userAccount);
    }

    @Override
    public ResponseAccountDto getAccountDtoById(Long accountId) throws AccountNotFoundException {
        return userAccountRepository.findByAccountId(accountId).map(userAccountMapper::toResponseAccountDto)
                .orElseThrow(() -> new AccountNotFoundException("No user account found for account id: " + accountId));
    }

    @Override
    public UserAccount getAccountById(Long accountId) throws AccountNotFoundException {
        return userAccountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountNotFoundException("No user account found for account id: " + accountId));
    }

    @Override
    public List<ResponseAccountDto> getAllIndividualAccounts() {
        List<UserAccount> individualAccounts = userAccountRepository.findByAccountType(AccountType.ROLE_INDIVIDUAL_ACCOUNT);
       return userAccountMapper.toResponseAccountDtoList(individualAccounts);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            Optional<UserAccount> accountOptional = userAccountRepository.findByUserName(username);
        return accountOptional.map(account -> new User(account.getUserName(), account.getPassword(), Collections.singleton(new SimpleGrantedAuthority(account.getAccountType().name()))))
                .orElseThrow(() -> new UsernameNotFoundException("No user account found for user name: " + username));
    }

    @Override
    public ResponseAccountDto upgradeToBusinessOwnerAccount(Long accountId) throws OperationNotAllowedException, AccountNotFoundException {
        UserAccount account = userAccountRepository.findByAccountId(accountId).orElseThrow(() -> new AccountNotFoundException("No user account found for account id: " + accountId));
        if(AccountType.ROLE_BUSINESS_OWNER_ACCOUNT.equals(account.getAccountType())){
            throw new OperationNotAllowedException("Account is already a business owner account.");
        }else if(AccountType.ROLE_SUB_ACCOUNT.equals(account.getAccountType())){
            throw new OperationNotAllowedException("Sub Account can not be upgraded to business owner account, please de link from business owner account and try again.");
        }
        LocalDateTime oneYearOlderDateFromNow = LocalDateTime.now().minusYears(1);
        //Check if account has made more than 10 orders within last 1 year
        List<Order> orders = orderRepository.getOrdersForIndividualAccountAfterGivenDate(accountId, oneYearOlderDateFromNow);
        if(CollectionUtils.isEmpty(orders) || orders.size() < MINIMUM_ORDER_REQUIRED_FOR_ACCOUNT_UPGRADE){
            throw new OperationNotAllowedException("Account is not eligible for upgrade.");
        }
        account.setAccountType(AccountType.ROLE_BUSINESS_OWNER_ACCOUNT);
        account = userAccountRepository.save(account);
        return userAccountMapper.toResponseAccountDto(account);
    }

    @Override
    public UserAccount findByUserName(String userName) {
        return userAccountRepository.findByUserName(userName).orElseThrow(() -> new UsernameNotFoundException("No user account found for userName: " + userName));
    }

    @Override
    public ResponseAccountDto deLinkSubAccountFromBusinessOwnerAccount(Long subAccountId) throws AccountNotFoundException {
        UserAccount account = userAccountRepository.findByAccountId(subAccountId).orElseThrow(() -> new AccountNotFoundException("No user account found for account id: " + subAccountId));
        if(AccountType.ROLE_INDIVIDUAL_ACCOUNT.equals(account.getAccountType())){
            throw new OperationNotAllowedException("Account is not linked to any business owner account.");
        }else if(AccountType.ROLE_BUSINESS_OWNER_ACCOUNT.equals(account.getAccountType())){
            throw new OperationNotAllowedException("Business Owner Account can not be downgraded to Individual account.");
        }
         ResponseAccountDto loggedInUser = LoggedInUser.get();
        //Only self user or parent account user can delink
        if(! (account.getAccountId().equals(loggedInUser.getAccountId()) || account.getParentAccount() != null && account.getParentAccount().getAccountId().equals(loggedInUser.getAccountId()))){
            throw new OperationNotAllowedException("Only the self SubAccount user or its parent user can de link account");
        }
        account.setAccountType(AccountType.ROLE_INDIVIDUAL_ACCOUNT);
        account.setParentAccount(null);
        account.setShareHistoricalOrders(null);
        account.setInitialDateToShare(null);
        account = userAccountRepository.save(account);
        return userAccountMapper.toResponseAccountDto(account);
    }

    @Override
    public List<UserAccount> getSubAccountsForBusinessAccount(Long parentAccountId) {
        UserAccount parentAccount = new UserAccount();
        parentAccount.setAccountId(parentAccountId);
        return userAccountRepository.findByAccountTypeAndParentAccount(AccountType.ROLE_SUB_ACCOUNT, parentAccount);
    }

}
