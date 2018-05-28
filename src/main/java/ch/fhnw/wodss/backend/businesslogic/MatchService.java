package ch.fhnw.wodss.backend.businesslogic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.fhnw.wodss.backend.domain.Bet;
import ch.fhnw.wodss.backend.domain.Match;
import ch.fhnw.wodss.backend.dto.MatchChangeDto;
import ch.fhnw.wodss.backend.dto.MatchDto;
import ch.fhnw.wodss.backend.dto.UserPublicDto;
import ch.fhnw.wodss.backend.mapping.BetMapper;
import ch.fhnw.wodss.backend.mapping.MatchMapper;
import ch.fhnw.wodss.backend.mapping.UserMapper;
import ch.fhnw.wodss.backend.messaging.MessageType;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;
import ch.fhnw.wodss.backend.persistence.BetRepository;
import ch.fhnw.wodss.backend.persistence.MatchRepository;
import ch.fhnw.wodss.backend.persistence.TeamRepository;
import ch.fhnw.wodss.backend.persistence.UserRepository;
import ch.fhnw.wodss.backend.security.UserPrincipal;

@Service
public class MatchService {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MatchRepository matchRepository;
	
	@Autowired
	private TeamRepository teamRepository;
	
	@Autowired
	private BetRepository betRepository;
	
	@Autowired
	private UserRepository userRepository;
	
    @Autowired
    private MatchMapper matchMapper;
	
    @Autowired
    private BetMapper betMapper;
	
    @Autowired
    private UserMapper userMapper;

	@Autowired
	private Validator validator;
	
	@Autowired
	private SimpMessagingTemplate webSocket;

	@Value("${wodss.messaging.global-update}")
	private String globalUpdate;

	@Value("${wodss.messaging.user-update}")
	private String userUpdate;

	/**
	 * Finds all matches and returns them as a list of DTOs.
	 * @return Returns the list of all matches.
	 */
	@PreAuthorize("hasRole('USER')")
	public List<MatchDto> findAll() {
		Sort sort = new Sort(Direction.ASC, "datetime");
		List<MatchDto> matches = matchRepository.findAll(sort)
				.stream()
				.map(match -> matchMapper.asMatchDto(match))
				.collect(Collectors.toList());
		log.info("Found " + matches.size() + " matches");
		return matches;
	}
	
	/**
	 * Changes the data of the given match. This method is only accessible, when the logged in user has the role admin.
	 * @param id The match to be changed.
	 * @param match The data to what the match should be changed.
	 * @return Returns the changed match.
	 * @throws NoSuchElementException Is thrown when the {@code id} does not exist.
	 * @throws IllegalArgumentException Is thrown when the passed DTO {@code match} is not valid.
	 */
	@PreAuthorize("hasRole('ADMIN')")
	public UpdateMessage update(Long id, MatchChangeDto match) throws NoSuchElementException, IllegalArgumentException {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// Check if match id exists
		Match oldMatch = matchRepository.getOne(id);
		if (oldMatch == null) {
			log.warn("Invalid match id received (uid=" + userId + ")");
			throw new NoSuchElementException("The provided match id does not exist");
		}
		
		// Check if match DTO is valid
		if (!validator.validate(match).isEmpty()) {
			log.warn("The provided MatchDto was invalid (uid=" + userId + ")");
			throw new IllegalArgumentException("The provided match DTO is not valid");
		}
		
		// Update match data
		oldMatch.setFinished(match.isFinished());
		if (match.getTeam1() != null) oldMatch.setTeam1(teamRepository.getOne(match.getTeam1().getCode()));
		if (match.getTeam2() != null) oldMatch.setTeam2(teamRepository.getOne(match.getTeam2().getCode()));
		oldMatch.setTeam1Score(match.getTeam1Score());
		oldMatch.setTeam2Score(match.getTeam2Score());
		oldMatch = matchRepository.save(oldMatch);
		
		// if isFinished was set to true
		if (match.isFinished()) {
			// Send updated bet with new points for each user with bet on this match
			for (Bet b : oldMatch.getBets()) {
				// Calculate and set points
				b = calculatePoints(b);
				// Send STOMP Message to user
				webSocket.convertAndSendToUser(b.getUser().getEmail(), userUpdate,
						new UpdateMessage(MessageType.UPDATED_BET, betMapper.asBetDto(b)));
			}
		}
		
		// Send STOMP Messages about changed users points and match
		UpdateMessage message = new UpdateMessage(MessageType.UPDATED_MATCH, matchMapper.asMatchDto(oldMatch));
		webSocket.convertAndSend(globalUpdate, message);
		List<UserPublicDto> users = userRepository.findAll().stream().map(u -> userMapper.asUserPublicDto(u)).collect(Collectors.toList());
		webSocket.convertAndSend(globalUpdate, new UpdateMessage(MessageType.UPDATED_USERS, users));
		
		log.info("Updated match with id " + id);
		return message;
	}

	private Bet calculatePoints(Bet b) {
		b = betRepository.getOne(b.getId());
		Match m = b.getMatch();
		// Scores correct
		if (b.getTeam1Score() == m.getTeam1Score() && b.getTeam2Score() == m.getTeam2Score()) {
			b.setPoints(4);
		} else {
			int points = 0;
			// Trend correct
			if ((b.getTeam1Score() > b.getTeam2Score() && m.getTeam1Score() > m.getTeam2Score())
				|| (b.getTeam1Score() == b.getTeam2Score() && m.getTeam1Score() == m.getTeam2Score())
				|| (b.getTeam1Score() < b.getTeam2Score() && m.getTeam1Score() < m.getTeam2Score())) {
				points += 1;
			}
			// Difference correct
			if (b.getTeam1Score() - b.getTeam2Score() == m.getTeam1Score() - m.getTeam2Score()) {
				points += 1;
			}
			b.setPoints(points);
		}
		return betRepository.save(b);
	}
}
