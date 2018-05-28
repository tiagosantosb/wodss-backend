package ch.fhnw.wodss.backend.businesslogic;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.fhnw.wodss.backend.domain.Bet;
import ch.fhnw.wodss.backend.domain.Match;
import ch.fhnw.wodss.backend.domain.User;
import ch.fhnw.wodss.backend.dto.BetChangeDto;
import ch.fhnw.wodss.backend.dto.BetDto;
import ch.fhnw.wodss.backend.dto.MatchDto;
import ch.fhnw.wodss.backend.mapping.BetMapper;
import ch.fhnw.wodss.backend.mapping.MatchMapper;
import ch.fhnw.wodss.backend.messaging.MessageType;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;
import ch.fhnw.wodss.backend.persistence.BetRepository;
import ch.fhnw.wodss.backend.persistence.MatchRepository;
import ch.fhnw.wodss.backend.persistence.UserRepository;
import ch.fhnw.wodss.backend.security.UserPrincipal;

@Service
public class BetService {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private BetRepository betRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MatchRepository matchRepository;
	
	@Autowired
	private BetMapper betMapper;
	
    @Autowired
    private MatchMapper matchMapper;
    
    @Autowired
    private Validator validator;
	
	@Autowired
	private SimpMessagingTemplate webSocket;

	@Value("${wodss.messaging.global-update}")
	private String globalUpdate;

	@Value("${wodss.messaging.user-update}")
	private String userUpdate;
	
	/**
	 * Finds all persisted bets for the currently logged in user (Spring Security User Principal).
	 * @return List of bet DTOs of the current user.
	 */
	@PreAuthorize("hasRole('USER')")
	public List<BetDto> findAll() {
		// Get user from session
		UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// Get bets for this user
		List<Bet> bets = betRepository.findByUserId(principal.getUserId());
		log.info("Found " + bets.size() + " bets for user with id " + principal.getUserId());
		return bets.stream()
				.map(bet -> betMapper.asBetDto(bet))
				.collect(Collectors.toList());
	}
	
	/**
	 * Creates a new bet for the currently logged in user.
	 * @param bet Takes a bet DTO with data (matchId, team1Score, team2Score).
	 * @return Returns the persisted bet as DTO.
	 * @throws IllegalArgumentException Is thrown when the provided bet is not valid.
	 * @throws IllegalStateException Is thrown when the match datetime is in the past.
	 */
	@PreAuthorize("hasRole('USER')")
	public UpdateMessage create(BetDto bet) throws IllegalArgumentException, IllegalStateException {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// Validate bet
		if (!validator.validate(bet).isEmpty()) {
			log.warn("Invalid BetDto received (uid=" + userId + ")");
			throw new IllegalArgumentException("The provided bet DTO is not valid.");
		}
		
		// Get user from session
		User user = userRepository.getOne(userId);
		
		// Check that datetime of match was not exceeded
		Match oldMatch = matchRepository.getOne(bet.getMatchId());
		if (oldMatch.getDatetime().before(new Date())) {
			log.warn("Received a bet for a match in the past (uid=" + userId + ")");
			throw new IllegalStateException("The start time of the match is in the past.");
		}

		// Create bet
		Bet newBet = new Bet();
		newBet.setUser(user);
		newBet.setMatch(oldMatch);
		newBet.setTeam1Score(bet.getTeam1Score());
		newBet.setTeam2Score(bet.getTeam2Score());
		newBet = betRepository.save(newBet);
		
		// Send notification for changed match statistics
		MatchDto match = matchMapper.asMatchDto(matchRepository.getOne(newBet.getMatch().getId()));
		webSocket.convertAndSend(globalUpdate, new UpdateMessage(MessageType.UPDATED_MATCH, match));
		// Send STOMP Message to user
		UpdateMessage message = new UpdateMessage(MessageType.NEW_BET, betMapper.asBetDto(newBet));
		webSocket.convertAndSendToUser(user.getEmail(), userUpdate, message);
		
		log.info("Created bet " + newBet.getId() + "(uid=" + userId + ")");
		return message;
	}
	
	/**
	 * Updates a bet of the currently logged in user.
	 * @param id The id of the bet to be changed.
	 * @param bet The DTO with the new bet data (team1Score, team2Score).
	 * @return Returns updated bet as DTO.
	 * @throws IllegalArgumentException Is thrown when the provided bet is not valid.
	 * @throws NoSuchElementException Is thrown when the bet id does not exist.
	 * @throws IllegalAccessException Is thrown when the bet with the passed {@code id} is not created by the currently logged in user.
	 * @throws IllegalStateException Is thrown when the match datetime is in the past.
	 */
	@PreAuthorize("hasRole('USER')")
	public UpdateMessage update(Long id, BetChangeDto bet) throws IllegalArgumentException, NoSuchElementException, IllegalAccessException, IllegalStateException {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// Validate bet
		if (!validator.validate(bet).isEmpty()) {
			log.warn("Invalid BetChangeDto received (uid=" + userId + ")");
			throw new IllegalArgumentException("The provided bet DTO is not valid");
		}
		
		// Check bet id exists
		Bet oldBet = betRepository.getOne(id);
		if (oldBet == null) {
			log.warn("Non existing bet id received (uid=" + userId + ")");
			throw new NoSuchElementException("Non existing bet id received");
		}
		
		// Check user permissions
		if (oldBet.getUser().getId() != userId) { 
			log.warn("User is not creator of bet (betId=" + id + ", uid=" + userId + ")");
			throw new IllegalAccessException("This user is not the creator of bet " + id);
		}

		// Check that datetime of match was not exceeded
		if (oldBet.getMatch().getDatetime().before(new Date())) {
			log.warn("Tried to update bet after match started (betId=" + id + ", uid=" + userId + ")");
			throw new IllegalStateException("The start time of the match is in the past.");
		}
		
		// Save new values
		oldBet.setTeam1Score(bet.getTeam1Score());
		oldBet.setTeam2Score(bet.getTeam2Score());
		oldBet = betRepository.save(oldBet);
		
		// Send notification for changed match statistics
		MatchDto match = matchMapper.asMatchDto(oldBet.getMatch());
		webSocket.convertAndSend(globalUpdate, new UpdateMessage(MessageType.UPDATED_MATCH, match));
		// Send STOMP Message to user
		UpdateMessage message = new UpdateMessage(MessageType.UPDATED_BET, betMapper.asBetDto(oldBet));
		webSocket.convertAndSendToUser(oldBet.getUser().getEmail(), userUpdate, message);
		
		log.info("Updated bet " + oldBet.getId() + " for user " + userId);
		return message;
	}
	
}
