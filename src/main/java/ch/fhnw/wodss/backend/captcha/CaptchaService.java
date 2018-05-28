package ch.fhnw.wodss.backend.captcha;

import java.net.URI;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

@Service
public class CaptchaService {

	@Autowired
	private CaptchaSettings captchaSettings;

	@Autowired
	private CaptchaAttemptService captchaAttemptService;

	@Autowired
	private RestOperations restTemplate;

    @Autowired
    private HttpServletRequest request;

	private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

	public boolean validate(String response) {
		String clientIp = request.getRemoteAddr();
		
		// Check response
		if (!responseSanityCheck(response)) return false;
		// Check attempts
		if (captchaAttemptService.isBlocked(clientIp)) return false;

		// Make request to Google
		URI verifyUri = URI.create(
				String.format(captchaSettings.getUrl(), captchaSettings.getSecret(), response, clientIp));
		GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);

		// Check Google response
		if (!googleResponse.isSuccess()) {
			// Only discredit user for failed attempt when client error occurred
			if (googleResponse.hasClientError()) captchaAttemptService.reCaptchaFailed(clientIp);
			return false;
		}
		return true;
	}

	private boolean responseSanityCheck(String response) {
		return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
	}
}
