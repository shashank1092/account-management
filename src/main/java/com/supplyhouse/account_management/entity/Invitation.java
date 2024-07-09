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

    @Column(name = "BUSINESS_OWNER_ACCOUNT_ID", nullable = false)
    private Long businessOwnerAccountId;

    @Column(name = "INDIVIDUAL_ACCOUNT_ID", nullable = false)
    private Long individualAccountId;

    @Column(name = "INVITATION_STATUS")
    @Enumerated(EnumType.STRING)
    private InvitationStatus invitationStatus;

    @Column(name = "SHARE_HISTORICAL_DATA")
    private Boolean shareHistoricalData;

    @Column(name = "CREATION_DATE", nullable = false,updatable = false)
    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column(name = "ACTION_PERFORMED_DATE", nullable = false)
    @CreationTimestamp
    private LocalDateTime actionPerformedDate;

}
