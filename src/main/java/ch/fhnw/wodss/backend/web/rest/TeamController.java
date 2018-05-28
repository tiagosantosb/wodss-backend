package ch.fhnw.wodss.backend.web.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.wodss.backend.businesslogic.TeamService;
import ch.fhnw.wodss.backend.dto.TeamDto;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
	
	@Autowired
	private TeamService teamService;
	
	@GetMapping
	public ResponseEntity<List<TeamDto>> findAll() {
		return new ResponseEntity<List<TeamDto>>(teamService.findAll(), HttpStatus.OK);
	}
}
