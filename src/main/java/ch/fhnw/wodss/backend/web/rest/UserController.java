package ch.fhnw.wodss.backend.web.rest;

import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.wodss.backend.businesslogic.UserService;
import ch.fhnw.wodss.backend.dto.UserCreateDto;
import ch.fhnw.wodss.backend.dto.UserDataChangeDto;
import ch.fhnw.wodss.backend.dto.UserPasswordResetRequestDto;
import ch.fhnw.wodss.backend.dto.UserPasswordChangeDto;
import ch.fhnw.wodss.backend.dto.UserPasswordResetDto;
import ch.fhnw.wodss.backend.dto.UserPrivateDto;
import ch.fhnw.wodss.backend.dto.UserPublicDto;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;
import ch.fhnw.wodss.backend.web.exception.ResourceException;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private HttpServletRequest request;
	
	@GetMapping
	public ResponseEntity<List<UserPublicDto>> findAll() {
		return new ResponseEntity<List<UserPublicDto>>(userService.findAll(), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<UpdateMessage> create(@RequestBody UserCreateDto user) {
		try {
			return new ResponseEntity<UpdateMessage>(userService.create(user, getClientIP(request)), HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.PRECONDITION_FAILED);
		} catch (IllegalStateException e) {
			throw new ResourceException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<UserPrivateDto> getOne(@PathVariable Long id) {
		try {
			return new ResponseEntity<UserPrivateDto>(userService.getOne(id), HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<UserPrivateDto>(HttpStatus.NOT_FOUND);
		} catch (IllegalAccessException e) {
			return new ResponseEntity<UserPrivateDto>(HttpStatus.UNAUTHORIZED);
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<UpdateMessage> updateData(@PathVariable Long id, @RequestBody UserDataChangeDto user) {
		try {
			return new ResponseEntity<UpdateMessage>(userService.updateData(id, user), HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.PRECONDITION_FAILED);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.NOT_FOUND);
		} catch (IllegalAccessException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.UNAUTHORIZED);
		}
	}
	
	@PostMapping("/{id}")
	public ResponseEntity<UserPrivateDto> updatePassword(@PathVariable Long id, @RequestBody UserPasswordChangeDto user) {
		try {
			return new ResponseEntity<UserPrivateDto>(userService.updatePassword(id, user), HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<UserPrivateDto>(HttpStatus.PRECONDITION_FAILED);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<UserPrivateDto>(HttpStatus.NOT_FOUND);
		} catch (IllegalStateException e) {
			throw new ResourceException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (IllegalAccessException e) {
			return new ResponseEntity<UserPrivateDto>(HttpStatus.UNAUTHORIZED);
		}
	}
	
	@PostMapping("/passwordreset/request")
	public ResponseEntity<Void> resetPasswordRequest(@RequestBody UserPasswordResetRequestDto email) {
		try {
			userService.resetPasswordRequest(email, getClientIP(request));
		} catch (Exception e) { 
			// Purposefully ignoring exceptions against data leakage
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@PostMapping("/passwordreset")
	public ResponseEntity<Void> resetPassword(@RequestBody UserPasswordResetDto reset) {
		try {
			userService.resetPassword(reset, getClientIP(request));
		} catch (Exception e) { 
			// When exceptions happen, only give an generic error
			throw new ResourceException(HttpStatus.BAD_REQUEST, "An unexpected error occured.");
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	private String getClientIP(HttpServletRequest request) {
	    String xfHeader = request.getHeader("X-Forwarded-For");
	    if (xfHeader == null){
	        return request.getRemoteAddr();
	    }
	    return xfHeader.split(",")[0];
	}
}
