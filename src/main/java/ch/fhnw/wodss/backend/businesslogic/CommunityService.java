package ch.fhnw.wodss.backend.businesslogic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
import org.springframework.stereotype.Service;

import ch.fhnw.wodss.backend.domain.Community;
import ch.fhnw.wodss.backend.domain.User;
import ch.fhnw.wodss.backend.dto.CommunityChangeDto;
import ch.fhnw.wodss.backend.dto.CommunityCreateDto;
import ch.fhnw.wodss.backend.dto.CommunityDataDto;
import ch.fhnw.wodss.backend.mapping.CommunityMapper;
import ch.fhnw.wodss.backend.messaging.MessageType;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;
import ch.fhnw.wodss.backend.persistence.CommunityRepository;
import ch.fhnw.wodss.backend.persistence.UserRepository;
import ch.fhnw.wodss.backend.security.UserPrincipal;

@Service
public class CommunityService {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CommunityMapper communityMapper;

	@Autowired
	private Validator validator;

	@Autowired
	private SimpMessagingTemplate webSocket;

	@Value("${wodss.messaging.global-update}")
	private String globalUpdate;

	@Value("${wodss.messaging.user-update}")
	private String userUpdate;
	
	PolicyFactory policy = new HtmlPolicyBuilder().toFactory();

	/**
	 * Finds all persisted communities.
	 * 
	 * @return Returns community DTOs with all the data, that the current user
	 *         is allowed to see. For each community: If the current user is
	 *         creator, he receives the private data else the public data.
	 */
	@PreAuthorize("hasRole('USER')")
	public List<CommunityDataDto> findAll() {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getUserId();
		Sort sort = new Sort(Direction.ASC, "name");
		List<CommunityDataDto> communities = communityRepository.findAll(sort).stream().map(community -> {
			if (userId.equals(community.getCreator().getId()))
				return (CommunityDataDto) communityMapper.asCommunityPrivateDto(community);
			else
				return (CommunityDataDto) communityMapper.asCommunityPublicDto(community);
		}).collect(Collectors.toList());
		log.info("Found " + communities.size() + " communities");
		return communities;
	}

	/**
	 * Creates a new community with the current user as creator.
	 * 
	 * @param community
	 *            Requires the community data as DTO.
	 * @return Returns the created community with private data as DTO.
	 * @throws IllegalArgumentException
	 *             Is thrown when the passed {@code community} is not valid.
	 */
	@PreAuthorize("hasRole('USER')")
	public UpdateMessage create(CommunityCreateDto community) throws IllegalArgumentException {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// Check if community is valid
		if (!validator.validate(community).isEmpty()) {
			log.warn("Invalid CommunityCreateDto received (uid=" + userId + ")");
			throw new IllegalArgumentException("The provided community DTO is not valid");
		}
		
		// Get logged in user
		User creator = userRepository.getOne(userId);

		Community newCommunity = new Community();
		newCommunity.setName(policy.sanitize(community.getName()));
		newCommunity.setCreator(creator);
		newCommunity = communityRepository.save(newCommunity);

		// Send update message with new community globally and to creator
		UpdateMessage message = new UpdateMessage(MessageType.NEW_COMMUNITY, communityMapper.asCommunityPublicDto(newCommunity));
		webSocket.convertAndSend(globalUpdate, message);
		message.setObject(communityMapper.asCommunityPrivateDto(newCommunity));
		webSocket.convertAndSendToUser(creator.getEmail(), userUpdate, message);

		log.info("Created community with id " + newCommunity.getId());
		return message;
	}

	/**
	 * Deletes a community if the currently logged in user is the creator.
	 * 
	 * @param id
	 *            The community to be used for the action.
	 * @throws NoSuchElementException
	 *             Is thrown when the {@code id} does not exist.
	 * @throws IllegalAccessException
	 *             If the current user is not the creator, a member or a join
	 *             requester, an IllegalAccessException is thrown.
	 */
	@PreAuthorize("hasRole('USER')")
	public UpdateMessage delete(Long id) throws NoSuchElementException, IllegalAccessException {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// Check if community id exists
		Community community = communityRepository.getOne(id);
		if (community == null) {
			log.warn("Invalid community id received (uid=" + userId + ")");
			throw new NoSuchElementException("Not existing community id");
		}

		// Check if user is creator
		handleAuthentication(id, community);

		// Send message to update topic
		UpdateMessage message = new UpdateMessage(MessageType.DELETED_COMMUNITY, communityMapper.asCommunityPublicDto(community));
		webSocket.convertAndSend(globalUpdate, message);
		communityRepository.deleteById(community.getId());
		return message;
	}

	/**
	 * Adds a join request to the community for the currently logged in user.
	 * 
	 * @param id
	 *            The id of the community to send the request to join to.
	 * @return Returns the community that received the join request.
	 * @throws NoSuchElementException
	 *             Is thrown when the community {@code id} does not exist.
	 * @throws IllegalArgumentException
	 *             Is thrown when the user is already a member or join
	 *             requester.
	 */
	@PreAuthorize("hasRole('USER')")
	public UpdateMessage createJoinRequest(Long id) throws NoSuchElementException, IllegalStateException {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// Check if community id exists
		Community community = communityRepository.getOne(id);
		if (community == null) {
			log.warn("Invalid community id received (uid=" + userId + ")");
			throw new NoSuchElementException("Not existing community id");
		}

		// Get session user
		User user = userRepository.getOne(userId);

		// Check if user is already a member/joinrequester
		if (community.getMembers().contains(user)) {
			log.warn("User is already member (communityId=" + id + ", uid=" + userId + ")");
			throw new IllegalStateException("This user is already a member.");
		}
		if (community.getJoinRequesters().contains(user)) {
			log.warn("User is already join requester (communityId=" + id + ", uid=" + userId + ")");
			throw new IllegalStateException("This user is already a joinRequester.");
		}

		community.addJoinRequester(user);
		community = communityRepository.save(community);

		// Send STOMP message to creator and user
		UpdateMessage message = new UpdateMessage(MessageType.UPDATED_COMMUNITY_JOINREQUESTS, communityMapper.asCommunityPrivateDto(community));
		webSocket.convertAndSendToUser(community.getCreator().getEmail(), userUpdate, message);
		message.setType(MessageType.UPDATED_COMMUNITY);
		message.setObject(communityMapper.asCommunityPublicDto(community));
		webSocket.convertAndSendToUser(user.getEmail(), userUpdate, message);

		log.info("Added user " + userId + " as JoinRequester for community " + id);
		return message;
	}

	/**
	 * Deletes a join request for a given community and join requester. This is
	 * only allowed to the join requester and community creator.
	 * 
	 * @param communityId
	 *            The id of the community.
	 * @param joinRequesterId
	 *            The id of the user that has a join request to be deleted.
	 * @return Returns the data for the corresponding requester, the creator
	 *         receives private data and the join requester receives public
	 *         data.
	 * @throws NoSuchElementException
	 *             Is thrown when the community {@code id} does not exist.
	 * @throws IllegalStateException
	 *             Is thrown when the user id is not in the join requesters list.
	 * @throws IllegalAccessException
	 *             Is thrown when anyone else than the creator or the join
	 *             requester tries to do this.
	 */
	@PreAuthorize("hasRole('USER')")
	public UpdateMessage deleteJoinRequest(Long communityId, Long joinRequesterId)
			throws NoSuchElementException, IllegalStateException, IllegalAccessException {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// Check if community id exists
		Community community = communityRepository.getOne(communityId);
		if (community == null) {
			log.warn("Invalid community id received (uid=" + userId + ")");
			throw new NoSuchElementException("Non existing community id received");
		}

		// Check if logged in user is either creator or the join requester
		boolean isCreator = false;
		try {
			handleAuthentication(communityId, community);
			isCreator = true;
		} catch (IllegalAccessException e) {
			if (joinRequesterId != userId) {
				log.warn("Tried to delete join request of other user (communityId=" + communityId + ", uid=" + userId + ")");
				throw e;
			}
		}

		// Check if join requester id exists
		Optional<User> user = community.getJoinRequesters().stream().filter(u -> u.getId() == joinRequesterId).findFirst();
		if (!user.isPresent()) {
			log.warn("Invalid join requester id received (communityId=" + communityId + ", uid=" + userId + ")");
			throw new IllegalStateException("There is no join requester with the id " + joinRequesterId);
		}

		community.removeJoinRequest(user.get());
		community = communityRepository.save(community);

		// Send STOMP message to creator and user
		UpdateMessage privateMessage = new UpdateMessage(MessageType.UPDATED_COMMUNITY_JOINREQUESTS, communityMapper.asCommunityPrivateDto(community));
		webSocket.convertAndSendToUser(community.getCreator().getEmail(), userUpdate, privateMessage);
		UpdateMessage publicMessage = new UpdateMessage(MessageType.UPDATED_COMMUNITY, communityMapper.asCommunityPublicDto(community));
		webSocket.convertAndSendToUser(user.get().getEmail(), userUpdate, publicMessage);

		if (isCreator) return privateMessage;
		else return publicMessage;
	}

	/**
	 * Accepts an user join request when the currently logged in user is the
	 * creator.
	 * 
	 * @param id
	 *            The id of the community.
	 * @param community
	 *            The community DTO that contains the user to be accepted.
	 * @return Returns the community private data as DTO.
	 * @throws IllegalArgumentException
	 *             Is thrown when the provided {@code community} DTO is not
	 *             valid.
	 * @throws NoSuchElementException
	 *             Is thrown when the provided {@code id} does not exist.
	 * @throws IllegalStateException
	 *             Is thrown when the provided user to be accepted is not in the
	 *             join requesters list.
	 * @throws IllegalAccessException
	 *             Is thrown when the currently logged in user is not the
	 *             creator of the community.
	 */
	@PreAuthorize("hasRole('USER')")
	public UpdateMessage acceptJoinRequest(Long id, CommunityChangeDto community)
			throws IllegalArgumentException, NoSuchElementException, IllegalStateException, IllegalAccessException {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// Validate community
		if (!validator.validate(community).isEmpty()) {
			log.warn("Invalid CommunityChangeDto received (uid=" + userId + ")");
			throw new IllegalArgumentException("The provided community DTO is not valid");
		}

		// Check community id exists
		Community oldCommunity = communityRepository.getOne(id);
		if (oldCommunity == null) {
			log.warn("Invalid community id received (uid=" + userId + ")");
			throw new NoSuchElementException("There is no community with that id");
		}

		// Check user permissions
		if (oldCommunity.getCreator().getId() != userId) {
			log.warn("User tried to access community without being creator (communityId=" + id + ", uid=" + userId + ")");
			throw new IllegalAccessException("This user is not the creator of community " + id);
		}

		// Check if user to be accepted is in join requesters list
		User newMember = userRepository.getOne(community.getAcceptMemberId());
		if (!oldCommunity.getJoinRequesters().contains(newMember)) {
			log.warn("User to accept is not a join requester (communityId=" + id + ", uid=" + userId + ")");
			throw new IllegalStateException("The member to be accepted is not in the join requesters list.");
		}
		
		// Accept user
		oldCommunity.acceptJoinRequest(newMember);
		oldCommunity = communityRepository.save(oldCommunity);

		// Send STOMP message globally
		UpdateMessage message = new UpdateMessage(MessageType.UPDATED_COMMUNITY, communityMapper.asCommunityPublicDto(oldCommunity));
		webSocket.convertAndSend(globalUpdate, message);
		// Send STOMP message to creator
		message.setType(MessageType.UPDATED_COMMUNITY_JOINREQUESTS);
		message.setObject(communityMapper.asCommunityPrivateDto(oldCommunity));
		webSocket.convertAndSendToUser(oldCommunity.getCreator().getEmail(), userUpdate, message);

		log.info("User " + userId + " joined the community with id " + id);
		return message;
	}

	/**
	 * Deletes a member from a given community. This is only allowed to the join
	 * requester and community creator.
	 * 
	 * @param communityId
	 *            The id of the community.
	 * @param memberId
	 *            The id of the member that should be deleted.
	 * @return Returns the data for the corresponding requester, the creator
	 *         receives private data and the member receives public
	 *         data.
	 * @throws NoSuchElementException
	 *             Is thrown when the community {@code id} does not exist.
	 * @throws IllegalStateException
	 *             Is thrown when the user id is not in the members list.
	 * @throws IllegalAccessException
	 *             Is thrown when anyone else than the creator or the join
	 *             requester tries to do this.
	 */
	@PreAuthorize("hasRole('USER')")
	public UpdateMessage deleteMember(Long communityId, Long memberId)
			throws NoSuchElementException, IllegalStateException, IllegalAccessException {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// Check if community id exists
		Community community = communityRepository.getOne(communityId);
		if (community == null) {
			log.warn("Invalid community id received (uid=" + userId + ")");
			throw new NoSuchElementException("There is no community with id = " + communityId);
		}

		// Check if logged in user is either creator or the member
		boolean isCreator = false;
		try {
			handleAuthentication(communityId, community);
			isCreator = true;
		} catch (IllegalAccessException e) {
			if (memberId != userId) {
				log.warn("User tried to delete membership of other user (communityId=" + communityId + ", uid=" + userId + ")");
				throw e;
			}
		}

		// Check if the member id exists
		Optional<User> user = community.getMembers().stream().filter(u -> u.getId() == memberId).findFirst();
		if (!user.isPresent()) {
			log.warn("The provided member id is not existing (communityId=" + communityId + ", uid=" + userId + ")");
			throw new IllegalStateException("There is no member with the id " + memberId);
		}	

		community.removeMember(user.get());
		community = communityRepository.save(community);

		// Send STOMP message globally and to creator
		UpdateMessage publicMessage = new UpdateMessage(MessageType.UPDATED_COMMUNITY, communityMapper.asCommunityPublicDto(community));
		webSocket.convertAndSend(globalUpdate, publicMessage);
		UpdateMessage privateMessage = new UpdateMessage(MessageType.UPDATED_COMMUNITY, communityMapper.asCommunityPrivateDto(community));
		webSocket.convertAndSendToUser(community.getCreator().getEmail(), userUpdate, privateMessage);

		if (isCreator) return privateMessage;
		else return publicMessage;
	}

	private void handleAuthentication(Long id, Community community) throws IllegalAccessException {
		// Check if logged in user is creator of community
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getUserId();
		if (userId != community.getCreator().getId()) {
			log.warn("User tried to access community without being creator (communityId=" + id + ", uid=" + userId + ")");
			throw new IllegalAccessException("No permission to this community");
		}
	}
}
