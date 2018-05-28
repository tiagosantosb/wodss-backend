package ch.fhnw.wodss.backend.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    private ResourceExceptionMapper mapper;

    @ExceptionHandler(ResourceException.class)
    public ResponseEntity<ResourceExceptionDto> handleException(ResourceException e) {
        log.warn("A ResourceException was thrown: " + e.getHttpStatus() + " - " + e.getUserMessage());
        return new ResponseEntity<ResourceExceptionDto>(mapper.convertToDto(e), e.getHttpStatus());
    }  

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResourceExceptionDto> handleException(DataIntegrityViolationException e) {
        log.warn("A DataIntegrityViolationException was thrown: " + e.getMessage());
        ResourceException re = new ResourceException(HttpStatus.BAD_REQUEST, "Ein Fehler ist aufgetreten.");
        return new ResponseEntity<ResourceExceptionDto>(mapper.convertToDto(re), re.getHttpStatus());
    }         
} 