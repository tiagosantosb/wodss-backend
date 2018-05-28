package ch.fhnw.wodss.backend.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
	String username() default "u1@test.ch";
	long userId() default 1L;
	String password() default "12345678";
	boolean isAdmin() default false;
}