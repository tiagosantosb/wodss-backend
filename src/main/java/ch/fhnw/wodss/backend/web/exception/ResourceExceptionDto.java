package ch.fhnw.wodss.backend.web.exception;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResourceExceptionDto {
	private HttpStatus httpStatus;
	private String userMessage;
}
