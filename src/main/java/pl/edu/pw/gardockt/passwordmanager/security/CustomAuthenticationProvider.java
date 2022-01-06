package pl.edu.pw.gardockt.passwordmanager.security;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pw.gardockt.passwordmanager.services.UserService;

import java.sql.Timestamp;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private final UserService userService;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, UserService userService) {
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);
        this.userService = userService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // get CustomUserDetails
        CustomUserDetails customUserDetails;
        try {
            customUserDetails = (CustomUserDetails) userDetails;
        } catch (ClassCastException e) {
            throw new AuthenticationServiceException("Invalid user details class");
        }

        Timestamp unlockDatetime = customUserDetails.getUser().getUnlockDatetime();

        if(unlockDatetime != null) {
            if(new Timestamp(System.currentTimeMillis()).after(unlockDatetime)) { // unlock account if the time has come
                userService.unlock(customUserDetails.getUsername());
            } else { // lock is on, deny authentication
                throw new LockedException("Account is locked due to too many failed attempts");
            }
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
        userService.resetFailedAttempts(user.getUsername());
        return super.createSuccessAuthentication(principal, authentication, user);
    }

}
