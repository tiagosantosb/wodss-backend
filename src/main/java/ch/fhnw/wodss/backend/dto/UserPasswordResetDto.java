package ch.fhnw.wodss.backend.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class UserPasswordResetDto {
	@NotNull
	@Email
	private String email;

	@NotNull
	@Size(min = 36, max = 36)
	private String token;

	@NotNull
	@Length(min=8)
	private String newPassword;
}
