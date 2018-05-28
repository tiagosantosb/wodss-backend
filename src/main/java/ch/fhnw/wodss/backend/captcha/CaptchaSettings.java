package ch.fhnw.wodss.backend.captcha;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "google.recaptcha")
@Data
public class CaptchaSettings {
    private String url;
    private String key;
    private String secret;
}
