package ch.fhnw.wodss.backend.businesslogic;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Validator;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.fhnw.wodss.backend.domain.PasswordResetToken;
import ch.fhnw.wodss.backend.domain.User;
import ch.fhnw.wodss.backend.dto.UserCreateDto;
import ch.fhnw.wodss.backend.dto.UserDataChangeDto;
import ch.fhnw.wodss.backend.dto.UserPasswordChangeDto;
import ch.fhnw.wodss.backend.dto.UserPasswordResetDto;
import ch.fhnw.wodss.backend.dto.UserPasswordResetRequestDto;
import ch.fhnw.wodss.backend.dto.UserPrivateDto;
import ch.fhnw.wodss.backend.dto.UserPublicDto;
import ch.fhnw.wodss.backend.mailing.EmailService;
import ch.fhnw.wodss.backend.mapping.UserMapper;
import ch.fhnw.wodss.backend.messaging.MessageType;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;
import ch.fhnw.wodss.backend.persistence.PasswordResetTokenRepository;
import ch.fhnw.wodss.backend.persistence.UserRepository;
import ch.fhnw.wodss.backend.security.UserPrincipal;

@Service
public class UserService {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private Validator validator;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private SimpMessagingTemplate webSocket;

	@Value("${wodss.messaging.global-update}")
	private String globalUpdate;

	@Value("${wodss.messaging.user-update}")
	private String userUpdate;
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Value("${wodss.server-url}")
	private String serverUrl;

	@Value("${wodss.security.passwordreset.expiration-time}")
	private int tokenExpirationTime;
	
	PolicyFactory policy = new HtmlPolicyBuilder().toFactory();

	/**
	 * Finds all persisted users.
	 * @return Returns a list of users with their public visible data as DTOs.
	 */
	@PreAuthorize("hasRole('USER')")
	public List<UserPublicDto> findAll() {
		Sort sort = new Sort(Direction.ASC, "id");
		List<User> users = userRepository.findAll(sort);
		log.debug("Found " + users.size() + " users");
		return users.stream().map(user -> userMapper.asUserPublicDto(user)).collect(Collectors.toList());
	}

	/**
	 * Creates a new user (register) with the given data.
	 * @param user User data as user entity.
	 * @return Returns created user with his private data as DTO.
	 * @throws IllegalArgumentException Is thrown when the provided {@code user} object is not valid.
	 * @throws IllegalStateException Is thrown when the provided email is already registered.
	 */
	public UpdateMessage create(UserCreateDto user, String ip) throws IllegalArgumentException, IllegalStateException {
		// Check if user entity is valid (also checks ReCaptcha over annotation)
		if (!validator.validate(user).isEmpty()) {
			log.warn("Received invalid UserCreateDto (ip=" + ip + ")");
			throw new IllegalArgumentException("User entity provided is not valid.");
		}

		// Check for used email
		if (userRepository.existsByEmail(user.getEmail())) {
			log.warn("Received already existing email address (ip=" + ip + ")");
			throw new IllegalStateException("Email already exists.");
		}

		// Encode password and save
		User newUser = new User();
		newUser.setEmail(user.getEmail());
		newUser.setName(policy.sanitize(user.getName()));
		newUser.setPassword(encoder.encode(user.getPassword()));
		newUser.setAdmin(false);
		newUser = userRepository.save(newUser);

		// Send STOMP Message
		UpdateMessage message = new UpdateMessage(MessageType.NEW_USER, userMapper.asUserPublicDto(newUser));
		webSocket.convertAndSend(globalUpdate, message);

		log.info("Created user with id=" + newUser.getId());
		message.setObject(userMapper.asUserPrivateDto(newUser));
		return message;
	}

	/**
	 * This method used to get the private user data as DTO for the currently logged in user.
	 * @param id The user id for which to get the private data.
	 * @return Returns the private user data as DTO.
	 * @throws NoSuchElementException Is thrown when the provided user {@code id} does not exist.
	 * @throws IllegalAccessException Is thrown when the currently logged in user has not the same id as was given in {@code id}.
	 */
	@PreAuthorize("hasRole('USER')")
	public UserPrivateDto getOne(Long id) throws NoSuchElementException, IllegalAccessException {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// Check if user id exists
		User user = userRepository.getOne(id);
		if (user == null) {
			log.warn("Invalid user id received (uid=" + userId + ")");
			throw new NoSuchElementException("No user exists with the given id.");
		}
		
		// Check if user logged in trying to update other user
		handleAuthentication(id);

		log.info("Found user with id " + id);
		return userMapper.asUserPrivateDto(user);
	}

	/**
	 * This method is used to change the data of the user (except the password).
	 * @param id The id of the user to be changed.
	 * @param user The new user data (except password).
	 * @return Returns the changed user with this private data as DTO.
	 * @throws IllegalArgumentException Is thrown when the provided {@code user} object is not valid.
	 * @throws NoSuchElementException Is thrown when the provided user {@code id} does not exist.
	 * @throws IllegalAccessException Is thrown when the currently logged in user has not the same id as was given in {@code id}.
	 */
	@PreAuthorize("hasRole('USER')")
	public UpdateMessage updateData(Long id, UserDataChangeDto user)
			throws IllegalArgumentException, NoSuchElementException, IllegalAccessException {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// Check if user data is valid
		if (!validator.validate(user).isEmpty()) {
			log.warn("Invalid UserDataChangeDto received (uid=" + userId + ")");
			throw new IllegalArgumentException("The given user DTO is not valid.");
		}

		// Check if user id exists
		User oldUser = userRepository.getOne(id);
		if (oldUser == null) {
			log.warn("Invalid user id received (uid=" + userId + ")");
			throw new NoSuchElementException("No user exists with the given id.");
		}

		// Check if user logged in is trying to update other user
		handleAuthentication(id);

		// Update user entity
		UpdateMessage message = new UpdateMessage(MessageType.UPDATED_USER, userMapper.asUserPublicDto(oldUser));
		if (!oldUser.getName().equals(user.getName())) {
			oldUser.setName(policy.sanitize(user.getName()));
			userRepository.save(oldUser);
			// Send STOMP Message globally
			message.setObject(userMapper.asUserPublicDto(oldUser));
			webSocket.convertAndSend(globalUpdate, message);
			// Send STOMP Message to user
			message.setObject(userMapper.asUserPrivateDto(oldUser));
			webSocket.convertAndSendToUser(oldUser.getEmail(), userUpdate, message);
		}

		log.info("User with id " + id + " has changed his data.");
		return message;
	}

	/**
	 * This method is to change the user password.
	 * @param id The user to be changed.
	 * @param user The user DTO containing the old and new password.
	 * @return Returns the changed user with this private data as DTO.
	 * @throws IllegalArgumentException Is thrown when the provided {@code user} object is not valid.
	 * @throws NoSuchElementException Is thrown when the provided user {@code id} does not exist.
	 * @throws IllegalAccessException Is thrown when the currently logged in user has not the same id as was given in {@code id}.
	 */
	@PreAuthorize("hasRole('USER')")
	public UserPrivateDto updatePassword(Long id, UserPasswordChangeDto user)
			throws IllegalArgumentException, NoSuchElementException, IllegalStateException, IllegalAccessException {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// Check if user data is valid
		if (!validator.validate(user).isEmpty()) {
			log.warn("Invalid UserPasswordChangeDto received (uid=" + userId + ")");
			throw new IllegalArgumentException("The given user DTO is not valid.");
		}

		// Check if user id exists
		User oldUser = userRepository.getOne(id);
		if (oldUser == null) {
			log.warn("Invalid user id received (uid=" + userId + ")");
			throw new NoSuchElementException("No user exists with the given id.");
		}

		// Check if user logged in is not admin or trying to update other user
		handleAuthentication(id);

		// Check old password
		if (!encoder.matches(user.getOldPassword(), oldUser.getPassword())) {
			log.warn("Invalid old password received (uid=" + userId + ")");
			throw new IllegalStateException("The old password is wrong.");
		}
		
		// Change the password
		oldUser.setPassword(encoder.encode(user.getNewPassword()));
		userRepository.save(oldUser);

		log.info("User with id " + id + " has changes his password.");
		return userMapper.asUserPrivateDto(oldUser);
	}
	
	/**
	 * For a valid and registered user email, a password reset token and email is generated and sent to the given email.
	 * @param email The email of the user to reset the password.
	 * @throws IllegalArgumentException Is thrown, when the DTO data is not valid.
	 * @throws NoSuchElementException Is thrown, when the email is not of a registered user.
	 */
	public void resetPasswordRequest(UserPasswordResetRequestDto email, String ip) throws IllegalArgumentException, NoSuchElementException {
		// Check if dto data is valid
		if (!validator.validate(email).isEmpty()) {
			log.warn("Invalid UserPasswordResetRequestDto received (ip=" + ip + ")");
			throw new IllegalArgumentException("The given DTO is not valid.");
		}	
		
		// Check if email exists
		User user = userRepository.findByEmail(email.getEmail());
		if (user == null) {
			log.warn("Invalid email received in resetPasswordRequest (ip=" + ip + ")");
			throw new NoSuchElementException("No user exists with this email.");
		}
		
		// Delete previous token if exists
		passwordResetTokenRepository.deleteByUserId(user.getId());
		
		// Generate token and save it
		PasswordResetToken token = new PasswordResetToken();
		token.setUser(user);
		token.setToken(UUID.randomUUID().toString());
		token.setExpiry(new Date(new Date().getTime() + tokenExpirationTime));
		token = passwordResetTokenRepository.save(token);
		log.info("Password reset token created with id=" + token.getId());
		
		// Send mail
		String resetUrl = serverUrl + "/passwortvergessen/" + token.getToken();
		String text = "<html><body><p>"
				+ "Hallo " + user.getName() + "<br>"
				+ "<br>"
				+ "Der Vorgang zur Zurücksetzung deines Passwortes wurde aktiviert. "
				+ "Falls du diesen Vorgang nicht getätigt hast, kannst du dieses Mail ignorieren."
				+ "Andernfalls kannst du mithilfe dieses Links dein Passwort zurücksetzen: <br>"
				+ "<a href=\"" + resetUrl + "\">" + resetUrl + "</a><br>"
				+ "<br>"
				+ "Freundliche Grüsse<br>"
				+ "Das wodss Tippspiel Team"
				+ "</p><body></html>";
		emailService.sendMessage(user.getEmail(), "wodss Tippspiel - Passwort zurücksetzen", text);
		log.info("Password reset mail was sent to " + user.getEmail());
	}
	
	/**
	 * Resets the user password if the given email and token combination is correct and the new password is secure.
	 * @param reset This is the data needed for the password reset.
	 * @throws IllegalArgumentException Is thrown, when the DTO data is not valid.
	 * @throws NoSuchElementException Is thrown, when the email is not of a registered user.
	 * @throws IllegalStateException Is thrown, when the token and email combination is incorrect.
	 */
	public void resetPassword(UserPasswordResetDto reset, String ip) throws IllegalArgumentException, NoSuchElementException, IllegalStateException {
		// Check if dto data is valid
		if (!validator.validate(reset).isEmpty()) {
			log.warn("Invalid UserPasswordResetDto received (ip=" + ip + ")");
			throw new IllegalArgumentException("The given DTO is not valid.");
		}
		
		// Check if email exists
		User user = userRepository.findByEmail(reset.getEmail());
		if (user == null) {
			log.warn("Invalid email received in resetPassword (ip=" + ip + ")");
			throw new NoSuchElementException("No user exists with this email.");
		}
		
		// Check the token and its expiration
		PasswordResetToken token = passwordResetTokenRepository.findFirstByTokenAndUserEmail(reset.getToken(), reset.getEmail());
		if (token == null || token.getExpiry().before(new Date())) {
			log.warn("Invalid reset token received (ip=" + ip + ")");
			throw new IllegalStateException("The token is invalid.");
		}
		passwordResetTokenRepository.delete(token);
		
		// Set the new password
		user.setPassword(encoder.encode(reset.getNewPassword()));
		user = userRepository.save(user);
		log.info("Password for user " + user.getId() + " was changed by password reset");
	}

	private void handleAuthentication(Long id) throws IllegalAccessException {
		// Check if user logged in is trying to update other user
		UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User sessionUser = userRepository.getOne(principal.getUserId());
		if (sessionUser.getId() != id) {
			log.warn("User tried to get access to an user other then himself (uid=" + sessionUser.getId() + ")");
			throw new IllegalAccessException("No access to this user.");
		}
	}
}
