package pl.edu.pw.gardockt.passwordmanager.security;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
		if(exception instanceof LockedException) {
			getRedirectStrategy().sendRedirect(request, response, "/login?locked");
		} else {
			getRedirectStrategy().sendRedirect(request, response, "/login?error");
		}
	}
}
