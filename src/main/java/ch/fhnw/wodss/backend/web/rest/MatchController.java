package ch.fhnw.wodss.backend.web.rest;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.wodss.backend.businesslogic.MatchService;
import ch.fhnw.wodss.backend.dto.MatchChangeDto;
import ch.fhnw.wodss.backend.dto.MatchDto;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;

@RestController
@RequestMapping("/api/matches")
public class MatchController {
	
	@Autowired
	private MatchService matchService;
	
	@GetMapping
	public ResponseEntity<List<MatchDto>> findAll() {
		return new ResponseEntity<List<MatchDto>>(matchService.findAll(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<UpdateMessage> update(@PathVariable Long id, @RequestBody MatchChangeDto match) {
		try {
			return new ResponseEntity<UpdateMessage>(matchService.update(id, match), HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.NOT_FOUND);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.PRECONDITION_FAILED);
		}
	}
}
