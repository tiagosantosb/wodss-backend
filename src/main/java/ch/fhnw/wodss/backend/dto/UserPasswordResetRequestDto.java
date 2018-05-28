package ch.fhnw.wodss.backend.dto;

import javax.validation.constraints.NotNull;

import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonProperty;

import ch.fhnw.wodss.backend.captcha.ValidCaptcha;
import lombok.Data;

@Data
public class UserPasswordResetRequestDto {
	@NotNull
	@Email
	private String email;
	
	@NotNull
	@ValidCaptcha
	@JsonProperty("g-recaptcha-response")
	private String reCaptchaResponse;
}
