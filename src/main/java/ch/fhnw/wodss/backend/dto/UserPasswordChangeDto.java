package ch.fhnw.wodss.backend.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class UserPasswordChangeDto {
	@NotNull
	@Length(min=8)
	private String oldPassword;

	@NotNull
	@Length(min=8)
	private String newPassword;
}
