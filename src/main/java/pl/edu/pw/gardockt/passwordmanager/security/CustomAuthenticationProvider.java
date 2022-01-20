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
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);
        this.userService = userService;
        this.databaseService = databaseService;
        this.request = request;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // get CustomUserDetails
        CustomUserDetails customUserDetails;
        try {
            customUserDetails = (CustomUserDetails) userDetails;
        } catch (ClassCastException e) {
            throw new InternalAuthenticationServiceException("Invalid user details class");
        }

        Timestamp unlockDatetime = customUserDetails.getUser().getUnlockDatetime();

        if(unlockDatetime != null) {
            if(new Timestamp(System.currentTimeMillis()).after(unlockDatetime)) { // unlock account if the time has come
                userService.unlock(customUserDetails.getUsername());
            } else { // lock is on, deny authentication
                throw new LockedException("Account is locked due to too many failed attempts");
            }
        }

        // prevent clients without User-Agent
        if(request.getHeader("User-Agent") == null) {
            throw new AccountStatusException("User-Agent not found") {};
        }

        try {
            super.additionalAuthenticationChecks(userDetails, authentication);
        } catch (AuthenticationException e) {
            userService.incrementFailedAttempts(customUserDetails.getUsername());
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

}
