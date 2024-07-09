package com.supplyhouse.account_management.dto;

import com.supplyhouse.account_management.enums.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponseDto {

    private Long invitationId;

    private Long businessOwnerAccountId;

    private Long individualAccountId;

    private Boolean shareHistoricalData;

    private InvitationStatus invitationStatus;

}
