package ch.fhnw.wodss.backend.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import ch.fhnw.wodss.backend.domain.User;
import ch.fhnw.wodss.backend.security.UserPrincipal;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		User user = new User();
		user.setEmail(customUser.username());
		user.setId(customUser.userId());
		user.setPassword(customUser.password());
		user.setAdmin(customUser.isAdmin());
		UserPrincipal principal = new UserPrincipal(user);
		Authentication auth = new UsernamePasswordAuthenticationToken(principal, customUser.password(),
				principal.getAuthorities());
		context.setAuthentication(auth);
		return context;
	}
}