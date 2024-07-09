package com.supplyhouse.account_management.dto;

import com.supplyhouse.account_management.enums.InvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvitationActionDto {

    @NotNull(message = "invitationId is mandatory")
    private Long invitationId;

    @NotNull(message = "shareHistoricalData is mandatory")
    private Boolean shareHistoricalData;

    @NotNull(message = "invitationStatus is mandatory")
    private InvitationStatus invitationStatus;

}
