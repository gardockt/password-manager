package pl.edu.pw.gardockt.passwordmanager.security;

import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pw.gardockt.passwordmanager.security.encryption.AES256GCMWithWaitEncryptionAlgorithm;
import pl.edu.pw.gardockt.passwordmanager.security.encryption.BCryptWithWaitPasswordEncoder;
import pl.edu.pw.gardockt.passwordmanager.security.encryption.EncryptionAlgorithm;
import pl.edu.pw.gardockt.passwordmanager.services.DatabaseService;
import pl.edu.pw.gardockt.passwordmanager.services.UserService;
import pl.edu.pw.gardockt.passwordmanager.views.LoginView;

import javax.servlet.http.HttpServletRequest;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private HttpServletRequest request;

    public final int failedAttemptsLockCount = 10;
    public final long lockTimeMillis = 5 * 60 * 1000;
    public final int authMinWaitTimeMillis = 100;
    public final int authMaxWaitTimeMillis = 500;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(new CustomAuthenticationProvider(userDetailsService, passwordEncoder, userService, databaseService, request));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        setLoginView(http, LoginView.class);
        http
            .authorizeRequests()
                .antMatchers("/login").permitAll()
                .and()
            .requiresChannel()
                .anyRequest().requiresSecure()
                .and()
            .formLogin()
                .failureHandler(new CustomAuthenticationFailureHandler());
        super.configure(http);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**");
        super.configure(web);
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptWithWaitPasswordEncoder(this);
    }

    @Bean
    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return new AES256GCMWithWaitEncryptionAlgorithm(this);
    }

}
