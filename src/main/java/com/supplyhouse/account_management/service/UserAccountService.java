package com.supplyhouse.account_management.service;

import com.supplyhouse.account_management.dto.InvitationResponseDto;
import com.supplyhouse.account_management.dto.RequestAccountDto;
import com.supplyhouse.account_management.dto.ResponseAccountDto;
import com.supplyhouse.account_management.entity.UserAccount;
import com.supplyhouse.account_management.exception.OperationNotAllowedException;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

public interface UserAccountService extends UserDetailsService {

    /**
     * Register new Individual Account
     * @param requestAccountDto instance of requestAccountDto
     * @return instance of ResponseAccountDto
     */
    ResponseAccountDto registerAccount(RequestAccountDto requestAccountDto);

    /**
     * Get Account details wrapped in DTO by account ID
     * @param accountId Account id
     * @return instance of ResponseAccountDto if found or else throw AccountNotFoundException
     */
    ResponseAccountDto getAccountDtoById(Long accountId) throws AccountNotFoundException;

    /**
     * Get Account details by account ID
     * @param accountId Account id
     * @return instance of UserAccount if found or else throw AccountNotFoundException
     */
    UserAccount getAccountById(Long accountId) throws AccountNotFoundException;

    /**
     * Get All Individual Accounts
     * @return List of Individual accounts List<ResponseAccountDto>
     */
    List<ResponseAccountDto> getAllIndividualAccounts();

    /**
     * Upgrade existing account to business owner account if eligible or else throw OperationNotAllowedException
     * @param accountId account id to be upgraded
     * @return upgraded account if eligible or else throws OperationNotAllowedException
     * @throws OperationNotAllowedException throws OperationNotAllowedException if account is not eligible for upgrade
     */
    ResponseAccountDto upgradeToBusinessOwnerAccount(Long accountId) throws OperationNotAllowedException, AccountNotFoundException;

    UserAccount findByUserName(String userName);

    ResponseAccountDto deLinkSubAccountFromBusinessOwnerAccount(Long subAccountId) throws AccountNotFoundException;

    List<UserAccount> getSubAccountsForBusinessAccount(Long accountId);
}
