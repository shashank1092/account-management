package com.supplyhouse.account_management.entity;

import com.supplyhouse.account_management.enums.InvitationStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "INVITATION")
public class Invitation {

    @Column(name = "INVITATION_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invitationId;

    @Column(name = "business_owner_account_id", nullable = false)
    private Long businessOwnerAccountId;

    @Column(name = "individual_account_id", nullable = false)
    private Long individualAccountId;

    @Column(name = "INVITATION_STATUS")
    @Enumerated(EnumType.STRING)
    private InvitationStatus invitationStatus;

    @Column(name = "share_historical_data")
    private Boolean shareHistoricalData;

    @Column(name = "CREATION_DATE", nullable = false,updatable = false)
    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column(name = "action_performed_date", nullable = false, updatable = true)
    @CreationTimestamp
    private LocalDateTime actionPerformedDate;

}
