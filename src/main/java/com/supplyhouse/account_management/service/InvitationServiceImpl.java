package com.supplyhouse.account_management.service;

import com.supplyhouse.account_management.dto.InvitationActionDto;
import com.supplyhouse.account_management.dto.InvitationResponseDto;
import com.supplyhouse.account_management.dto.ResponseAccountDto;
import com.supplyhouse.account_management.entity.Invitation;
import com.supplyhouse.account_management.entity.UserAccount;
import com.supplyhouse.account_management.enums.AccountType;
import com.supplyhouse.account_management.enums.InvitationStatus;
import com.supplyhouse.account_management.exception.InvitationExpiredException;
import com.supplyhouse.account_management.exception.InvitationNotFoundException;
import com.supplyhouse.account_management.exception.OperationNotAllowedException;
import com.supplyhouse.account_management.mapper.InvitationMapper;
import com.supplyhouse.account_management.repository.InvitationRepository;
import com.supplyhouse.account_management.repository.UserAccountRepository;
import com.supplyhouse.account_management.security.util.LoggedInUser;
import jakarta.transaction.Transactional;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class InvitationServiceImpl implements InvitationService{

    private final InvitationMapper invitationMapper = Mappers.getMapper(InvitationMapper.class);

    private final InvitationRepository invitationRepository;

    private final UserAccountRepository userAccountRepository;

    private final UserAccountService userAccountService;

    @Autowired
    public InvitationServiceImpl(InvitationRepository invitationRepository, UserAccountRepository userAccountRepository, UserAccountService userAccountService){
        this.invitationRepository=invitationRepository;
        this.userAccountRepository=userAccountRepository;
        this.userAccountService=userAccountService;
    }

    @Override
    public InvitationResponseDto inviteAccountToLinkToBusinessAccount(Long individualAccountId) throws AccountNotFoundException {
        Optional<UserAccount> accountOptional = userAccountRepository.findById(individualAccountId);
        UserAccount account = accountOptional.orElseThrow(() -> new AccountNotFoundException("No user account found for account id: " + individualAccountId));
        if(! account.getAccountType().equals(AccountType.ROLE_INDIVIDUAL_ACCOUNT)){
            throw new OperationNotAllowedException("You can only send invite to a user having an Individual account type but found account type as: "+ account.getAccountType().name());
        }
        Invitation invitation = new Invitation();
        invitation.setInvitationStatus(InvitationStatus.PENDING);
        //get logged in user
        ResponseAccountDto loggedInUser = LoggedInUser.get();
        invitation.setBusinessOwnerAccountId(loggedInUser.getAccountId());
        invitation.setIndividualAccountId(individualAccountId);
        invitation = invitationRepository.save(invitation);
        return invitationMapper.toInvitationResponseDto(invitation);
    }

    @Override
    public InvitationResponseDto acceptOrRejectInvitation(InvitationActionDto invitationActionDto) throws AccountNotFoundException {
        Invitation invitation = invitationRepository.findById(invitationActionDto.getInvitationId()).orElseThrow(() -> new InvitationNotFoundException("No invitation found for invitationId: "+ invitationActionDto.getInvitationId()));
        //If invitation is not in Pending state
        if(!invitation.getInvitationStatus().equals(InvitationStatus.PENDING)){
            throw new OperationNotAllowedException("Invalid state of Invitation currentState: "+invitation.getInvitationStatus().name());
        }
        //Check if Invitation has expired
        if(invitation.getCreationDate().isBefore(LocalDateTime.now().minusHours(24))){
            throw new InvitationExpiredException("The invitation has expired, please request the Business owner to send a new invite.");
        }
        //Check if user is same
        ResponseAccountDto loggedInUser = LoggedInUser.get();
        if(! invitation.getIndividualAccountId().equals(loggedInUser.getAccountId())){
            throw new BadCredentialsException("User is not authorised to perform action on this Invitation: "+invitation.getInvitationId());
        }
        invitation.setInvitationStatus(invitationActionDto.getInvitationStatus());
        invitation.setShareHistoricalData(invitationActionDto.getShareHistoricalData());
        //invitation.setActionPerformedDate(now);
        if(invitationActionDto.getInvitationStatus().equals(InvitationStatus.ACCEPTED)){
            UserAccount individualAccount = userAccountService.getAccountById(invitation.getIndividualAccountId());
            UserAccount parentAccount = userAccountService.getAccountById(invitation.getBusinessOwnerAccountId());
            individualAccount.setParentAccount(parentAccount);
            boolean shareHistoricalOrders = invitationActionDto.getShareHistoricalData();
            individualAccount.setShareHistoricalOrders(shareHistoricalOrders);
            individualAccount.setAccountType(AccountType.ROLE_SUB_ACCOUNT);
            if(! shareHistoricalOrders){
                LocalDateTime now = LocalDateTime.now();
                individualAccount.setInitialDateToShare(now);
            }
            userAccountRepository.save(individualAccount);
            invitation = invitationRepository.save(invitation);
        }
        return invitationMapper.toInvitationResponseDto(invitation);
    }
}
