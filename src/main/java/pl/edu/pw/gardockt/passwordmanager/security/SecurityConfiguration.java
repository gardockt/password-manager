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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pw.gardockt.passwordmanager.security.encryption.AES256GCMEncryptionAlgorithm;
import pl.edu.pw.gardockt.passwordmanager.security.encryption.EncryptionAlgorithm;
import pl.edu.pw.gardockt.passwordmanager.services.UserService;
import pl.edu.pw.gardockt.passwordmanager.views.LoginView;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    // TODO: move somewhere else?
    public final int failedAttemptsLockCount = 10;
    public final long lockTimeMillis = 5 * 60 * 1000;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(new CustomAuthenticationProvider(userDetailsService, passwordEncoder, userService));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**");
        super.configure(web);
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return new AES256GCMEncryptionAlgorithm();
    }

}
