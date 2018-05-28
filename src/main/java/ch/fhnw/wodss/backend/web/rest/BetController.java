package ch.fhnw.wodss.backend.web.rest;

import java.util.List;
import java.util.NoSuchElementException;

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

import ch.fhnw.wodss.backend.businesslogic.BetService;
import ch.fhnw.wodss.backend.dto.BetChangeDto;
import ch.fhnw.wodss.backend.dto.BetDto;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;
import ch.fhnw.wodss.backend.web.exception.ResourceException;

@RestController
@RequestMapping("/api/bets")
public class BetController {
	
	@Autowired
	private BetService betService;
	
	@GetMapping
	public ResponseEntity<List<BetDto>> findAll() {
		return new ResponseEntity<List<BetDto>>(betService.findAll(), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<UpdateMessage> create(@RequestBody BetDto bet) {
		try {
			return new ResponseEntity<UpdateMessage>(betService.create(bet), HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.PRECONDITION_FAILED);
		} catch (IllegalStateException e) {
			throw new ResourceException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<UpdateMessage> update(@PathVariable Long id, @RequestBody BetChangeDto bet) {
		try {
			return new ResponseEntity<UpdateMessage>(betService.update(id, bet), HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.PRECONDITION_FAILED);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.NOT_FOUND);
		} catch (IllegalAccessException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.UNAUTHORIZED);
		}
	}
}
