package pl.edu.pw.gardockt.passwordmanager.security;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import pl.edu.pw.gardockt.passwordmanager.services.DatabaseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final DatabaseService databaseService;

	public CustomAuthenticationFailureHandler(DatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
		if(exception instanceof LockedException) {
			getRedirectStrategy().sendRedirect(request, response, "/login?locked");
		} else {
			databaseService.incrementFailedAttempts(request.getRemoteAddr());
			getRedirectStrategy().sendRedirect(request, response, "/login?error");
		}
	}
}
