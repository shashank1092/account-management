package com.supplyhouse.account_management.service;

import com.supplyhouse.account_management.dto.InvitationActionDto;
import com.supplyhouse.account_management.dto.InvitationResponseDto;

import javax.security.auth.login.AccountNotFoundException;

public interface InvitationService {
    /**
     * Send invite to user
     * @param individualAccountId
     * @return InvitationResponseDto
     */
    InvitationResponseDto inviteAccountToLinkToBusinessAccount(Long individualAccountId) throws AccountNotFoundException;

    //InvitationResponseDto cancelInvitation(UUID invitationId);
    InvitationResponseDto acceptOrRejectInvitation(InvitationActionDto invitationActionDto) throws AccountNotFoundException;

}
