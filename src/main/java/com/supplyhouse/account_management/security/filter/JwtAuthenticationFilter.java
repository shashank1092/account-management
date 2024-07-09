package com.supplyhouse.account_management.security.filter;

import com.supplyhouse.account_management.dto.ResponseAccountDto;
import com.supplyhouse.account_management.entity.UserAccount;
import com.supplyhouse.account_management.enums.AccountType;
import com.supplyhouse.account_management.mapper.UserAccountMapper;
import com.supplyhouse.account_management.security.util.JWTHelper;
import com.supplyhouse.account_management.security.util.LoggedInUser;
import com.supplyhouse.account_management.service.UserAccountService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserAccountMapper userAccountMapper = Mappers.getMapper(UserAccountMapper.class);
    private final JWTHelper jwtHelper;

    private final UserAccountService userAccountService;

    @Autowired
    public JwtAuthenticationFilter(JWTHelper jwtHelper, UserAccountService userAccountService){
        this.jwtHelper=jwtHelper;
        this.userAccountService=userAccountService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Extracting token from the request header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extracting the token from the Authorization header
            token = authHeader.substring(7);
            // Extracting username from the token
            userName = jwtHelper.getUsernameFromToken(token);
        }

        // If username is extracted and there is no authentication in the current SecurityContext
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Loading UserDetails by username extracted from the token
            UserDetails userDetails = userAccountService.loadUserByUsername(userName);

            // Validating the token with loaded UserDetails
            if (jwtHelper.validateToken(token, userDetails)) {
                // Creating an authentication token using UserDetails
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // Setting authentication details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Setting the authentication token in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
                //Set user account to Thread local
                setUserAccountToThreadLocal(userDetails);
            }
        }

        // Proceeding with the filter chain
        filterChain.doFilter(request, response);

        //Clear the thread local after request is processed
        LoggedInUser.clear();
    }

    private void setUserAccountToThreadLocal(UserDetails userDetails) {
        String userName = userDetails.getUsername();
        UserAccount account = userAccountService.findByUserName(userName);
        ResponseAccountDto userAccount = userAccountMapper.toResponseAccountDto(account);
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        Optional<? extends GrantedAuthority> grantedAuthorityOptional = authorities.stream().findFirst();
        grantedAuthorityOptional.ifPresent(grantedAuthority -> userAccount.setAccountType(AccountType.valueOf(grantedAuthority.getAuthority())));
        LoggedInUser.set(userAccount);
    }
}
