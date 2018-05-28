package ch.fhnw.wodss.backend.web.exception;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class ResourceException extends RuntimeException {
	private static final long serialVersionUID = -3050441006923974080L;
	
	private HttpStatus httpStatus;
	private String userMessage;
	
	public ResourceException(HttpStatus httpStatus, String userMessage) {
		this.httpStatus = httpStatus;
		this.userMessage = userMessage;
	}
}
