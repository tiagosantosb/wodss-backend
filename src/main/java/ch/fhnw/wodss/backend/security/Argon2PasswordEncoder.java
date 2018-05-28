package ch.fhnw.wodss.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import ch.fhnw.wodss.backend.web.exception.ResourceException;

import static com.kosprov.jargon2.api.Jargon2.*;

public class Argon2PasswordEncoder implements PasswordEncoder {
	@Value("${wodss.security.argon2.type}")
	private Type type;
	
	@Value("${wodss.security.argon2.memoryCost}")
	private int memoryCost;
	
	@Value("${wodss.security.argon2.timeCost}")
	private int timeCost;
	
	@Value("${wodss.security.argon2.parallelism}")
	private int parallelism;

	@Value("${wodss.security.argon2.saltLength}")
	private int saltLength;

	@Value("${wodss.security.argon2.hashLength}")
	private int hashLength;

	@Override
	public String encode(final CharSequence rawPassword) {
		if (rawPassword == null || "".equals(rawPassword)) throw new ResourceException(HttpStatus.BAD_REQUEST, "Password must not be empty");
		
		return jargon2Hasher()
                .type(type)
                .memoryCost(memoryCost)
                .timeCost(timeCost)
                .parallelism(parallelism)
                .saltLength(saltLength)
                .hashLength(hashLength)
				.password(rawPassword.toString().getBytes())
				.encodedHash();
	}

	@Override
	public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
		if (rawPassword == null || "".equals(rawPassword)) throw new ResourceException(HttpStatus.UNAUTHORIZED, "Password must not be empty");
		
        Verifier verifier = jargon2Verifier();
		return verifier
				.hash(encodedPassword)
				.password(rawPassword.toString().getBytes())
				.verifyEncoded();
	}

}
