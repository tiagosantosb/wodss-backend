package ch.fhnw.wodss.backend.businesslogic;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ch.fhnw.wodss.backend.dto.TeamDto;
import ch.fhnw.wodss.backend.mapping.TeamMapper;
import ch.fhnw.wodss.backend.persistence.TeamRepository;

@Service
public class TeamService {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired 
	private TeamRepository teamRepository;
	
	@Autowired
	private TeamMapper teamMapper;
	
	/**
	 * Finds all teams ordered by the team code.
	 * @return Returns a list of all teams as DTOs.
	 */
	@PreAuthorize("hasRole('USER')")
	public List<TeamDto> findAll() {
		Sort sort = new Sort(Direction.ASC, "code");
		List<TeamDto> teams = teamRepository.findAll(sort)
				.stream()
				.map(team -> teamMapper.asTeamDto(team))
				.collect(Collectors.toList());
		log.info("Found " + teams.size() + " teams");
		return teams;
	}
}
