package pl.edu.pw.gardockt.passwordmanager.security;

import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import pl.edu.pw.gardockt.passwordmanager.services.DatabaseService;
import pl.edu.pw.gardockt.passwordmanager.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private final UserService userService;
    private final DatabaseService databaseService;

    private final HttpServletRequest request;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, UserService userService, DatabaseService databaseService, HttpServletRequest request) {
        this.userService = userService;
        this.databaseService = databaseService;
        this.request = request;

        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);

        setPreAuthenticationChecks(this::preAuthenticationChecks);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        try {
            super.additionalAuthenticationChecks(userDetails, authentication);
        } catch (AuthenticationException e) {
            userService.incrementFailedAttempts(userDetails.getUsername());
            throw e;
        }
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails) authentication.getDetails();
        userService.resetFailedAttempts(user.getUsername());
        databaseService.addLoginHistory(((CustomUserDetails)user).getUser(), webAuthenticationDetails.getRemoteAddress(), request.getHeader("User-Agent"));
        return super.createSuccessAuthentication(principal, authentication, user);
    }

    private void preAuthenticationChecks(UserDetails userDetails) {
        databaseService.getIpLock(request.getRemoteAddr()).ifPresent(l -> {
            if(l.getUnlockDatetime() != null) {
                if(new Timestamp(System.currentTimeMillis()).after(l.getUnlockDatetime())) { // unlock IP if the time has come
                    databaseService.deleteIpLock(l);
                } else {
                    throw new LockedException("IP is locked due to too many failed attempts");
                }
            }
        });

        // prevent clients without User-Agent
        if(request.getHeader("User-Agent") == null) {
            // unfortunately, only few exceptions interrupt authentication and this is probably the most fitting one
            throw new AccountStatusException("User-Agent not found") {};
        }
    }

}
