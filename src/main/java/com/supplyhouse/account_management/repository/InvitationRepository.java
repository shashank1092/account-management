package com.supplyhouse.account_management.repository;

import com.supplyhouse.account_management.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    @Query("From Invitation i where i.individualAccountId = :individualAccountId")
    List<Invitation> getPendingInvitationsForAccount(Long individualAccountId);

}
