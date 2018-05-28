package ch.fhnw.wodss.backend.captcha;

import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CaptchaConstraintValidator implements ConstraintValidator<ValidCaptcha, String> {

    @Autowired
    private CaptchaService captchaService;

    @Override
    public void initialize(ValidCaptcha constraintAnnotation) { }

    @Override
    public boolean isValid(String reCaptchaResponse, ConstraintValidatorContext context) {
        if (reCaptchaResponse == null || reCaptchaResponse.isEmpty()) {
            return false;
        }
        return captchaService.validate(reCaptchaResponse);
    }

}