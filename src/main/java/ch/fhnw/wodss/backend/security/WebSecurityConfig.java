package ch.fhnw.wodss.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	@Autowired
	private RestAuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	private RestAuthenticationFailureHandler authenticationFailureHandler;
	
	@Autowired
	private UserSecurityService userDetailsService;

	@Bean
	public RestAuthenticationSuccessHandler createSuccessHandler() {
		return new RestAuthenticationSuccessHandler();
	}

	@Bean
	public RestAuthenticationFailureHandler createFailureHandler() {
		return new RestAuthenticationFailureHandler();
	}
	 
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    authProvider.setUserDetailsService(userDetailsService);
	    authProvider.setPasswordEncoder(encoder());
	    return authProvider;
	}
	 
	@Bean
	public PasswordEncoder encoder() {
	    return new Argon2PasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	    auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.httpBasic().disable()
			// HTTPS only
			.requiresChannel().anyRequest().requiresSecure()
			.and()
			// Exceptions in authentication return HTTP 401 error
			.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint)
			.and()
			// Allow registration and password reset to unauthenticated users
			.csrf().disable()
			.authorizeRequests().antMatchers(HttpMethod.POST, 
					"/api/users", 
					"/api/users/passwordreset/request",
					"/api/users/passwordreset")
			.anonymous()
			.and()
			// Allow all API URLs to authenticated users only
			.authorizeRequests().antMatchers("/api/**").authenticated()
			.and()
			// Allow access to swagger documentation to admins only
			.authorizeRequests().antMatchers(
					"/api/swagger", 
					"/api/swagger-ui", 
					"/swagger-ui.html",
					"/webjars/**")
			.hasRole("ADMIN")
			.and()
			// Allow login to all requesters with given success handler and failure handler
			.formLogin()
				.loginProcessingUrl("/api/login")
				.usernameParameter("email")
				.successHandler(authenticationSuccessHandler)
				.failureHandler(authenticationFailureHandler)
				.permitAll()
			.and()
			// Allow logout to all users
			.logout()
				.logoutUrl("/api/logout")
				.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
			.and()
			// Allow access to /h2 to admins only
			.authorizeRequests().antMatchers("/h2/**").hasRole("ADMIN")
			.and()
			.headers().frameOptions().disable()
			.and()
			// PermitAll access to frontend
			.authorizeRequests().antMatchers("/**").permitAll()
			.and()
			// Send HSTS in Header
			.headers().httpStrictTransportSecurity();
	}
}
