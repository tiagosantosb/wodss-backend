package ch.fhnw.wodss.backend.businesslogic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import ch.fhnw.wodss.backend.config.SpringBootConfig;
import ch.fhnw.wodss.backend.config.WithMockCustomUser;
import ch.fhnw.wodss.backend.domain.Community;
import ch.fhnw.wodss.backend.domain.User;
import ch.fhnw.wodss.backend.dto.CommunityChangeDto;
import ch.fhnw.wodss.backend.dto.CommunityCreateDto;
import ch.fhnw.wodss.backend.dto.CommunityDataDto;
import ch.fhnw.wodss.backend.dto.CommunityPrivateDto;
import ch.fhnw.wodss.backend.dto.CommunityPublicDto;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;
import ch.fhnw.wodss.backend.persistence.CommunityRepository;
import ch.fhnw.wodss.backend.persistence.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootConfig.class })
public class CommunityServiceTest {
	public final static long CREATOR_ID = 1L;
	public final static String CREATOR_NAME = "creator";
	public final static String CREATOR_EMAIL = "creator@test.ch";
	public final static String CREATOR_PASSWORD = "12345678";
	public final static boolean CREATOR_IS_ADMIN = false;

	public final static long MEMBER_ID = 2L;
	public final static String MEMBER_NAME = "member";
	public final static String MEMBER_EMAIL = "member@test.ch";
	public final static String MEMBER_PASSWORD = "12345678";
	public final static boolean MEMBER_IS_ADMIN = false;

	public final static long REQUESTER_ID = 3L;
	public final static String REQUESTER_NAME = "requester";
	public final static String REQUESTER_EMAIL = "requester@test.ch";
	public final static String REQUESTER_PASSWORD = "12345678";
	public final static boolean REQUESTER_IS_ADMIN = false;

	public final static long ILLEGAL_ID = 4L;
	public final static String ILLEGAL_NAME = "illegal";
	public final static String ILLEGAL_EMAIL = "illegal@test.ch";
	public final static String ILLEGAL_PASSWORD = "12345678";
	public final static boolean ILLEGAL_IS_ADMIN = false;

	public final static long C1_ID = 1L;
	public final static String C1_NAME = "community1";

	private Community c1;
	private Community c1copy;
	private User creator;
	private User member;
	private User requester;
	private User illegalUser;

	@MockBean
	private CommunityRepository communityRepositoryMock;

	@MockBean
	private UserRepository userRepositoryMock;

	@MockBean
	private SimpMessagingTemplate webSocketMock;

	@Autowired
	@InjectMocks
	private CommunityService communityService;

	@Before
	public void setUp() {
		Mockito.reset(communityRepositoryMock);

		creator = new User();
		creator.setId(CREATOR_ID);
		creator.setName(CREATOR_NAME);
		creator.setEmail(CREATOR_EMAIL);
		creator.setPassword(CREATOR_PASSWORD);
		creator.setAdmin(CREATOR_IS_ADMIN);

		member = new User();
		member.setId(MEMBER_ID);
		member.setName(MEMBER_NAME);
		member.setEmail(MEMBER_EMAIL);
		member.setPassword(MEMBER_PASSWORD);
		member.setAdmin(MEMBER_IS_ADMIN);

		requester = new User();
		requester.setId(REQUESTER_ID);
		requester.setName(REQUESTER_NAME);
		requester.setEmail(REQUESTER_EMAIL);
		requester.setPassword(REQUESTER_PASSWORD);
		requester.setAdmin(REQUESTER_IS_ADMIN);

		illegalUser = new User();
		illegalUser.setId(ILLEGAL_ID);
		illegalUser.setName(ILLEGAL_NAME);
		illegalUser.setEmail(ILLEGAL_EMAIL);
		illegalUser.setPassword(ILLEGAL_PASSWORD);
		illegalUser.setAdmin(ILLEGAL_IS_ADMIN);

		Mockito.when(userRepositoryMock.getOne(CREATOR_ID)).thenReturn(creator);
		Mockito.when(userRepositoryMock.getOne(MEMBER_ID)).thenReturn(member);
		Mockito.when(userRepositoryMock.getOne(REQUESTER_ID)).thenReturn(requester);
		Mockito.when(userRepositoryMock.getOne(ILLEGAL_ID)).thenReturn(illegalUser);

		Set<User> members = new HashSet<>();
		members.add(creator);
		members.add(member);

		Set<User> membersCopy = new HashSet<>();
		membersCopy.add(creator);
		membersCopy.add(member);

		Set<User> joinRequesters = new HashSet<>();
		joinRequesters.add(requester);

		Set<User> joinRequestersCopy = new HashSet<>();
		joinRequestersCopy.add(requester);

		c1 = new Community();
		c1.setId(C1_ID);
		c1.setCreator(creator);
		c1.setName(C1_NAME);
		c1.setCreator(creator);
		c1.setMembers(members);
		c1.setJoinRequesters(joinRequesters);

		c1copy = new Community();
		c1copy.setId(C1_ID);
		c1copy.setCreator(creator);
		c1copy.setName(C1_NAME);
		c1copy.setCreator(creator);
		c1copy.setMembers(membersCopy);
		c1copy.setJoinRequesters(joinRequestersCopy);

		List<Community> communities = new LinkedList<>();
		communities.add(c1);

		Mockito.when(communityRepositoryMock.getOne(C1_ID)).thenReturn(c1);
		Mockito.when(communityRepositoryMock.findAll(new Sort(Direction.ASC, "name"))).thenReturn(communities);
		Mockito.when(communityRepositoryMock.save(Mockito.any())).thenReturn(c1copy);
	}

	@Test
	@WithMockCustomUser(userId = CREATOR_ID, username = CREATOR_EMAIL, password = CREATOR_PASSWORD)
	public void findAll_ok() {
		// Method call
		List<CommunityDataDto> dtos = communityService.findAll();

		// Verification
		assertTrue(dtos.size() == 1);
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).findAll(new Sort(Direction.ASC, "name"));
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
		Mockito.verifyZeroInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = CREATOR_ID, username = CREATOR_EMAIL, password = CREATOR_PASSWORD)
	public void create_ok() {
		// Data preparation
		CommunityCreateDto createDto = new CommunityCreateDto();
		createDto.setName("ValidName");
		Set<User> members = new HashSet<>();
		members.add(creator);
		Set<User> joinRequesters = new HashSet<>();
		Community newCommunity = new Community();
		newCommunity.setId(2L);
		newCommunity.setCreator(creator);
		newCommunity.setName(createDto.getName());
		newCommunity.setCreator(creator);
		newCommunity.setMembers(members);
		newCommunity.setJoinRequesters(joinRequesters);
		Mockito.when(communityRepositoryMock.save(Mockito.any())).thenReturn(newCommunity);

		// Method call
		UpdateMessage message = communityService.create(createDto);
		CommunityPrivateDto dto = (CommunityPrivateDto) message.getObject();

		// Verification
		assertTrue(dto.getName().equals(createDto.getName()) && dto.getCreator() == CREATOR_ID);
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).save(Mockito.any());
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(CREATOR_ID);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = CREATOR_ID, username = CREATOR_EMAIL, password = CREATOR_PASSWORD)
	public void create_nameTooShort() {
		// Data preparation
		CommunityCreateDto createDto = new CommunityCreateDto();
		createDto.setName("12");

		// Method call
		try {
			communityService.create(createDto);
			fail("IllegalArgumentException was expected");
		} catch (IllegalArgumentException e) {
		}

		// Verification
		Mockito.verifyZeroInteractions(communityRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = CREATOR_ID, username = CREATOR_EMAIL, password = CREATOR_PASSWORD)
	public void create_nameTooLong() {
		// Data preparation
		CommunityCreateDto createDto = new CommunityCreateDto();
		createDto.setName("jr1j9fc4cfr32j4cf4r2h348rfjd423f842dj4jf91d2j3d941924jd912j4rc312fcrf24rf23r2dfefd2");

		// Method call
		try {
			communityService.create(createDto);
			fail("IllegalArgumentException was expected");
		} catch (IllegalArgumentException e) {
		}

		// Verification
		Mockito.verifyZeroInteractions(communityRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = CREATOR_ID, username = CREATOR_EMAIL, password = CREATOR_PASSWORD)
	public void delete_ok() {
		// Method call
		CommunityPublicDto dto = null;
		try {
			UpdateMessage message = communityService.delete(C1_ID);
			dto = (CommunityPublicDto) message.getObject();
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException was not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException was not expected");
		}

		// Verification
		assertEquals(C1_ID, (long) dto.getId());
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(C1_ID);
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).deleteById(C1_ID);
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = CREATOR_ID, username = CREATOR_EMAIL, password = CREATOR_PASSWORD)
	public void delete_idDoesNotExist() {
		// Data preparation
		long unusedId = C1_ID + 100;

		// Method call
		try {
			communityService.delete(unusedId);
			fail("NoSuchElementException was expected");
		} catch (NoSuchElementException e) {
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException was not expected");
		}

		// Verification
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(unusedId);
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = ILLEGAL_ID, username = ILLEGAL_EMAIL, password = ILLEGAL_PASSWORD)
	public void delete_illegalUser() {
		// Method call
		try {
			communityService.delete(C1_ID);
			fail("IllegalAccessException was expected");
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException was not expected");
		} catch (IllegalAccessException e) {
		}
	}

	@Test
	@WithMockCustomUser(userId = ILLEGAL_ID, username = ILLEGAL_EMAIL, password = ILLEGAL_PASSWORD)
	public void createJoinRequest_ok() {
		// Method call
		UpdateMessage message = communityService.createJoinRequest(C1_ID);
		CommunityPublicDto dto = (CommunityPublicDto) message.getObject();

		// Verification
		assertTrue(dto.getId() == C1_ID);
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(C1_ID);
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).save(Mockito.any());
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(ILLEGAL_ID);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = REQUESTER_ID, username = REQUESTER_EMAIL, password = REQUESTER_PASSWORD)
	public void createJoinRequest_idDoesNotExist() {
		// Data preparation
		long unusedId = C1_ID + 100;

		// Method call
		try {
			communityService.createJoinRequest(unusedId);
			fail("NoSuchElementException was expected");
		} catch (NoSuchElementException e) {
		}

		// Verification
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(unusedId);
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = MEMBER_ID, username = MEMBER_EMAIL, password = MEMBER_PASSWORD)
	public void createJoinRequest_alreadyMember() {
		// Method call
		try {
			communityService.createJoinRequest(C1_ID);
			fail("IllegalStateException was expected");
		} catch (IllegalStateException e) {
		}

		// Verification
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(C1_ID);
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(MEMBER_ID);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = REQUESTER_ID, username = REQUESTER_EMAIL, password = REQUESTER_PASSWORD)
	public void createJoinRequest_alreadyRequested() {
		// Method call
		try {
			communityService.createJoinRequest(C1_ID);
			fail("IllegalStateException was expected");
		} catch (IllegalStateException e) {
		}

		// Verification
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(C1_ID);
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(REQUESTER_ID);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = REQUESTER_ID, username = REQUESTER_EMAIL, password = REQUESTER_PASSWORD)
	public void deleteJoinRequest_byRequester_ok() {
		// Data preparation
		c1copy.getJoinRequesters().remove(requester);

		// Method call
		CommunityDataDto dto = null;
		try {
			UpdateMessage message = communityService.deleteJoinRequest(C1_ID, REQUESTER_ID);
			dto = (CommunityDataDto) message.getObject();
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException was not expected");
		} catch (IllegalStateException e) {
			fail("IllegalStateException was not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException was not expected");
		}

		// Verification
		assertTrue(dto instanceof CommunityPublicDto);
		assertTrue(communityRepositoryMock.getOne(c1.getId()).getJoinRequesters().isEmpty());
		Mockito.verify(communityRepositoryMock, Mockito.times(2)).getOne(C1_ID);
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).save(Mockito.any());
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = CREATOR_ID, username = CREATOR_EMAIL, password = CREATOR_PASSWORD)
	public void deleteJoinRequest_byCreator_ok() {
		// Data preparation
		c1copy.getJoinRequesters().clear();

		// Method call
		CommunityDataDto dto = null;
		try {
			UpdateMessage message = communityService.deleteJoinRequest(C1_ID, REQUESTER_ID);
			dto = (CommunityDataDto) message.getObject();
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException was not expected");
		} catch (IllegalStateException e) {
			fail("IllegalStateException was not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException was not expected");
		}

		// Verification
		assertTrue(((CommunityPrivateDto) dto).getJoinRequesters().isEmpty());
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(C1_ID);
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).save(Mockito.any());
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = ILLEGAL_ID, username = ILLEGAL_EMAIL, password = ILLEGAL_PASSWORD)
	public void deleteJoinRequest_illegalAccess() {
		// Method call
		try {
			communityService.deleteJoinRequest(C1_ID, REQUESTER_ID);
			fail("IllegalAccessException was expected");
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException was not expected");
		} catch (IllegalStateException e) {
			fail("IllegalStateException was not expected");
		} catch (IllegalAccessException e) {
		}
	}

	@Test
	@WithMockCustomUser(userId = REQUESTER_ID, username = REQUESTER_EMAIL, password = REQUESTER_PASSWORD)
	public void deleteJoinRequest_commutnityIdDoesNotExist() {
		// Data preparation
		long unusedId = C1_ID + 100;

		// Method call
		try {
			communityService.deleteJoinRequest(unusedId, REQUESTER_ID);
			fail("NoSuchElementException was expected");
		} catch (NoSuchElementException e) {
		} catch (IllegalStateException e) {
			fail("IllegalStateException was not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException was not expected");
		}

		// Verification
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(unusedId);
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = REQUESTER_ID, username = REQUESTER_EMAIL, password = REQUESTER_PASSWORD)
	public void deleteJoinRequest_requesterIdDoesNotExist() {
		// Data preparation
		c1.removeJoinRequest(requester);

		// Method call
		try {
			communityService.deleteJoinRequest(C1_ID, REQUESTER_ID);
			fail("IllegalStateException was expected");
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException was not expected");
		} catch (IllegalStateException e) {
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException was not expected");
		}

		// Verification
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(C1_ID);
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = CREATOR_ID, username = CREATOR_EMAIL, password = CREATOR_PASSWORD)
	public void acceptJoinRequest_ok() {
		// Data preparation
		CommunityChangeDto changeDto = new CommunityChangeDto();
		changeDto.setAcceptMemberId(REQUESTER_ID);
		c1copy.getJoinRequesters().remove(requester);
		c1copy.getMembers().add(requester);

		// Method call
		CommunityPrivateDto dto = null;
		try {
			UpdateMessage message = communityService.acceptJoinRequest(C1_ID, changeDto);
			dto = (CommunityPrivateDto) message.getObject();
		} catch (IllegalArgumentException e) {
			fail("IllegalArgumentException was not expected");
		} catch (NoSuchElementException e) {
			fail("IllegalArgumentException was not expected");
		} catch (IllegalStateException e) {
			fail("IllegalArgumentException was not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalArgumentException was not expected");
		}

		// Verification
		assertTrue(0 == dto.getJoinRequesters().stream().filter(uid -> uid == requester.getId()).count());
		assertTrue(1 == dto.getMembers().stream().filter(uid -> uid == requester.getId()).count());
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(C1_ID);
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).save(Mockito.any());
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(REQUESTER_ID);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = CREATOR_ID, username = CREATOR_EMAIL, password = CREATOR_PASSWORD)
	public void acceptJoinRequest_memberIdNull() {
		// Data preparation
		CommunityChangeDto changeDto = new CommunityChangeDto();
		changeDto.setAcceptMemberId(null);

		// Method call
		try {
			communityService.acceptJoinRequest(C1_ID, changeDto);
			fail("IllegalArgumentException was expected");
		} catch (IllegalArgumentException e) {
		} catch (NoSuchElementException e) {
			fail("IllegalArgumentException was not expected");
		} catch (IllegalStateException e) {
			fail("IllegalArgumentException was not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalArgumentException was not expected");
		}
	}

	@Test
	@WithMockCustomUser(userId = CREATOR_ID, username = CREATOR_EMAIL, password = CREATOR_PASSWORD)
	public void acceptJoinRequest_communityIdDoesNotExist() {
		// Data preparation
		CommunityChangeDto changeDto = new CommunityChangeDto();
		changeDto.setAcceptMemberId(REQUESTER_ID);
		long unusedId = C1_ID + 100;

		// Method call
		try {
			communityService.acceptJoinRequest(unusedId, changeDto);
			fail("NoSuchElementException was expected");
		} catch (IllegalArgumentException e) {
			fail("IllegalArgumentException was not expected");
		} catch (NoSuchElementException e) {
		} catch (IllegalStateException e) {
			fail("IllegalArgumentException was not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalArgumentException was not expected");
		}

		// Verification
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(unusedId);
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = CREATOR_ID, username = CREATOR_EMAIL, password = CREATOR_PASSWORD)
	public void acceptJoinRequest_memberNotInRequestorList() {
		// Data preparation
		CommunityChangeDto changeDto = new CommunityChangeDto();
		changeDto.setAcceptMemberId(ILLEGAL_ID);

		// Method call
		try {
			communityService.acceptJoinRequest(C1_ID, changeDto);
			fail("IllegalStateException was expected");
		} catch (IllegalArgumentException e) {
			fail("IllegalArgumentException was not expected");
		} catch (NoSuchElementException e) {
			fail("IllegalArgumentException was not expected");
		} catch (IllegalStateException e) {
		} catch (IllegalAccessException e) {
			fail("IllegalArgumentException was not expected");
		}

		// Verification
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(ILLEGAL_ID);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = ILLEGAL_ID, username = ILLEGAL_EMAIL, password = ILLEGAL_PASSWORD)
	public void acceptJoinRequest_notCreator() {
		// Data preparation
		CommunityChangeDto changeDto = new CommunityChangeDto();
		changeDto.setAcceptMemberId(REQUESTER_ID);

		// Method call
		try {
			communityService.acceptJoinRequest(C1_ID, changeDto);
			fail("IllegalAccessException was expected");
		} catch (IllegalArgumentException e) {
			fail("IllegalArgumentException was not expected");
		} catch (NoSuchElementException e) {
			fail("IllegalArgumentException was not expected");
		} catch (IllegalStateException e) {
			fail("IllegalArgumentException was not expected");
		} catch (IllegalAccessException e) {
		}
	}

	@Test
	@WithMockCustomUser(userId = MEMBER_ID, username = MEMBER_EMAIL, password = MEMBER_PASSWORD)
	public void deleteMember_byMember_ok() {
		// Data preparation
		c1copy.getMembers().remove(member);

		// Method call
		CommunityPublicDto dto = null;
		try {
			UpdateMessage message = communityService.deleteMember(C1_ID, MEMBER_ID);
			dto = (CommunityPublicDto) message.getObject();
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException was not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException was not expected");
		}

		// Verification
		assertTrue(0 == dto.getMembers().stream().filter(uid -> uid == MEMBER_ID).count());
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(C1_ID);
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).save(Mockito.any());
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = CREATOR_ID, username = CREATOR_EMAIL, password = CREATOR_PASSWORD)
	public void deleteMember_byCreator_ok() {
		// Data preparation
		c1copy.getMembers().remove(member);

		// Method call
		CommunityDataDto dto = null;
		try {
			UpdateMessage message = communityService.deleteMember(C1_ID, MEMBER_ID);
			dto = (CommunityPrivateDto) message.getObject();
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException was not expected");
		} catch (IllegalStateException e) {
			fail("IllegalStateException was not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException was not expected");
		}

		// Verification
		assertFalse(((CommunityPrivateDto) dto).getMembers().contains(MEMBER_ID));
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(C1_ID);
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).save(Mockito.any());
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = CREATOR_ID, username = CREATOR_EMAIL, password = CREATOR_PASSWORD)
	public void deleteMember_communityIdDoesNotExist() {
		// Data preparation
		long unusedId = C1_ID + 100;

		// Method call
		try {
			communityService.deleteMember(unusedId, MEMBER_ID);
			fail("NoSuchElementException was expected");
		} catch (NoSuchElementException e) {
		} catch (IllegalStateException e) {
			fail("IllegalStateException was not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException was not expected");
		}

		// Verification
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(unusedId);
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = CREATOR_ID, username = CREATOR_EMAIL, password = CREATOR_PASSWORD)
	public void deleteMember_memberIdNotInMembers() {
		// Data preparation
		long unusedId = MEMBER_ID + 100;

		// Method call
		try {
			communityService.deleteMember(C1_ID, unusedId);
			fail("IllegalStateException was expected");
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException was not expected");
		} catch (IllegalStateException e) {
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException was not expected");
		}

		// Verification
		Mockito.verify(communityRepositoryMock, Mockito.times(1)).getOne(C1_ID);
		Mockito.verifyNoMoreInteractions(communityRepositoryMock);
	}

	@Test
	@WithMockCustomUser(userId = ILLEGAL_ID, username = ILLEGAL_EMAIL, password = ILLEGAL_PASSWORD)
	public void deleteMember_illegalAccess() {
		// Method call
		try {
			communityService.deleteMember(C1_ID, MEMBER_ID);
			fail("IllegalAccessException was expected");
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException was not expected");
		} catch (IllegalStateException e) {
			fail("IllegalStateException was not expected");
		} catch (IllegalAccessException e) {
		}
	}
}
