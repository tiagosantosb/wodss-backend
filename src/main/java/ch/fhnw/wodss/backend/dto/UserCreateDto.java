package ch.fhnw.wodss.backend.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import ch.fhnw.wodss.backend.captcha.ValidCaptcha;
import lombok.Data;

@Data
public class UserCreateDto {
	@NotNull
	@Email
	private String email;
	
	@NotNull
	@Length(min=8)
	private String password;
	
	@NotNull
	@Length(min=3, max=20)
	private String name;
	
	@NotNull
	@ValidCaptcha
	@JsonProperty("g-recaptcha-response")
	private String reCaptchaResponse;
}
