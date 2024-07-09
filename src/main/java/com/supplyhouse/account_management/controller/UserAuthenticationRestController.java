package com.supplyhouse.account_management.controller;

import com.supplyhouse.account_management.dto.LoginRequestDto;
import com.supplyhouse.account_management.dto.RequestAccountDto;
import com.supplyhouse.account_management.dto.ResponseAccountDto;
import com.supplyhouse.account_management.security.util.JWTHelper;
import com.supplyhouse.account_management.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("auth")
@Tag(name = "Authentication", description = "Endpoints for account creation and user authentication")
public class UserAuthenticationRestController {

    UserAccountService userAccountService;

    JWTHelper jwtHelper;
    AuthenticationProvider authenticationProvider;

    @Autowired
    public UserAuthenticationRestController(UserAccountService userAccountService, JWTHelper jwtHelper, AuthenticationProvider authenticationProvider){
        this.userAccountService=userAccountService;
        this.jwtHelper=jwtHelper;
        this.authenticationProvider=authenticationProvider;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new account", description = "Creates a new user account and returns the account details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseAccountDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    public ResponseEntity<ResponseAccountDto> registerAccount(@RequestBody @Validated  RequestAccountDto requestAccountDto) {
        ResponseAccountDto responseDto = userAccountService.registerAccount(requestAccountDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login to an existing account", description = "Authenticates the user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))}),
            @ApiResponse(responseCode = "401", description = "Bad credentials, Invalid username or password", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    public ResponseEntity<Map<String,String>> loginAccount(@RequestBody @Validated LoginRequestDto loginRequestDto) {
        //UserDetails userDetails = userAccountService.loadUserByUsername(loginRequestDto.getUserName());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getUserName(), loginRequestDto.getPassword());
        Authentication authentication = authenticationProvider.authenticate(authenticationToken);
        String jwtToken = jwtHelper.generateToken(((User)authentication.getPrincipal()).getUsername());
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", jwtToken);
        return new ResponseEntity<>(tokenMap, HttpStatus.OK);
    }



}
