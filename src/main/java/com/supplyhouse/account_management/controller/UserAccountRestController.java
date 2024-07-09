package com.supplyhouse.account_management.controller;

import com.supplyhouse.account_management.dto.InvitationActionDto;
import com.supplyhouse.account_management.dto.InvitationResponseDto;
import com.supplyhouse.account_management.dto.ResponseAccountDto;
import com.supplyhouse.account_management.exception.OperationNotAllowedException;
import com.supplyhouse.account_management.service.InvitationService;
import com.supplyhouse.account_management.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@RequestMapping(("account"))
@Tag(name = "Account", description = "Endpoints for account details")
public class UserAccountRestController {

    UserAccountService userAccountService;

    InvitationService invitationService;

    @Autowired
    public UserAccountRestController(UserAccountService userAccountService, InvitationService invitationService){
        this.userAccountService=userAccountService;
        this.invitationService=invitationService;
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get account details by id", description = "Get account details for the given account id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account fetched successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseAccountDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorised", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    public ResponseEntity<ResponseAccountDto> getAccountById(@PathVariable @NotNull Long id) throws AccountNotFoundException {
        ResponseAccountDto responseAccountDto = userAccountService.getAccountDtoById(id);
        return new ResponseEntity<>(responseAccountDto, HttpStatus.OK);
    }

    @PutMapping("/{accountId}/upgrade")
    @Operation(summary = "Upgrade existing account to business owner account", description = "This api is to request for account upgrade to a business owner account if eligible")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account upgraded to a business owner account successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseAccountDto.class))}),
            @ApiResponse(responseCode = "400", description = "Account is not eligible for upgrade", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorised", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    public ResponseEntity<ResponseAccountDto> upgradeToBusinessOwnerAccount(@PathVariable @NotNull Long accountId) throws OperationNotAllowedException, AccountNotFoundException {
        ResponseAccountDto responseDto = userAccountService.upgradeToBusinessOwnerAccount(accountId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/individual-accounts")
    @Operation(summary = "Get individual accounts", description = "Get all individual accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account fetched successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseAccountDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorised", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    public ResponseEntity<List<ResponseAccountDto>> getAllIndividualAccounts() {
        List<ResponseAccountDto> responseAccountDtoList = userAccountService.getAllIndividualAccounts();
        return new ResponseEntity<>(responseAccountDtoList, HttpStatus.OK);
    }

    @GetMapping("/{accountId}/invite")
    @PreAuthorize("hasRole('ROLE_BUSINESS_OWNER_ACCOUNT')")
    @Operation(summary = "Invite Individual Account to a Business Account", description = "Send Invite to an Individual account to join as a subAccount to a Business account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation sent successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseAccountDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorised", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    public ResponseEntity<InvitationResponseDto> inviteAccountToLinkToBusinessAccount(@PathVariable @NotNull Long accountId) throws AccountNotFoundException {
        InvitationResponseDto responseDto = invitationService.inviteAccountToLinkToBusinessAccount(accountId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/invite/action")
    @PreAuthorize("hasRole('ROLE_INDIVIDUAL_ACCOUNT')")
    @Operation(summary = "Accept or Reject an Invitation", description = "Individual Account owner can either accepts or rejects the invitation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account linked to Business Account successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseAccountDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorised", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    public ResponseEntity<InvitationResponseDto> acceptOrRejectInvitation(@RequestBody InvitationActionDto invitationActionDto) throws AccountNotFoundException {
        InvitationResponseDto responseDto = invitationService.acceptOrRejectInvitation(invitationActionDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{subAccountId}/delink")
    @PreAuthorize("hasAnyRole('ROLE_SUB_ACCOUNT', 'ROLE_BUSINESS_OWNER_ACCOUNT')")
    @Operation(summary = "De link sub account from business owner account", description = "This api is to request for account de link from a business owner account if eligible")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account de linked from a business owner account successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseAccountDto.class))}),
            @ApiResponse(responseCode = "400", description = "Account is not eligible for de link", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorised", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    public ResponseEntity<ResponseAccountDto> deLinkSubAccountFromBusinessOwnerAccount(@PathVariable @NotNull Long subAccountId) throws OperationNotAllowedException, AccountNotFoundException {
        ResponseAccountDto responseDto = userAccountService.deLinkSubAccountFromBusinessOwnerAccount(subAccountId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
