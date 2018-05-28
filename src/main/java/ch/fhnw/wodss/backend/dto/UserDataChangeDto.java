package ch.fhnw.wodss.backend.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class UserDataChangeDto {
	@NotNull
	@Length(min=3, max=20)
	private String name;
}
