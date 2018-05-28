package ch.fhnw.wodss.backend.businesslogic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.springframework.test.context.junit4.SpringRunner;

import ch.fhnw.wodss.backend.config.SpringBootConfig;
import ch.fhnw.wodss.backend.config.WithMockCustomUser;
import ch.fhnw.wodss.backend.domain.Bet;
import ch.fhnw.wodss.backend.domain.Match;
import ch.fhnw.wodss.backend.domain.Stadium;
import ch.fhnw.wodss.backend.domain.Team;
import ch.fhnw.wodss.backend.domain.User;
import ch.fhnw.wodss.backend.dto.MatchChangeDto;
import ch.fhnw.wodss.backend.dto.MatchDto;
import ch.fhnw.wodss.backend.dto.StadiumDto;
import ch.fhnw.wodss.backend.dto.TeamDto;
import ch.fhnw.wodss.backend.mapping.MatchMapper;
import ch.fhnw.wodss.backend.mapping.UserMapper;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;
import ch.fhnw.wodss.backend.persistence.MatchRepository;
import ch.fhnw.wodss.backend.persistence.TeamRepository;
import ch.fhnw.wodss.backend.persistence.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootConfig.class })
public class MatchServiceTest {
	public final static long ADMIN_ID = 1L;
	public final static String ADMIN_NAME = "admin";
	public final static String ADMIN_EMAIL = "admin@test.ch";
	public final static String ADMIN_PASSWORD = "admin123";
	public final static boolean ADMIN_IS_ADMIN = true;

	private Team t1;
	private Team t2;
	private Match m1;
	private Match m1copy;
	private Match m1copy2;
	private MatchDto m1Dto;

	@Autowired
	@InjectMocks
	private MatchService matchService;

	@MockBean
	private MatchRepository matchRepositoryMock;

	@MockBean
	private TeamRepository teamRepositoryMock;

	@MockBean
	private UserRepository userRepositoryMock;

	@MockBean
	private MatchMapper matchMapperMock;

	@MockBean
	private UserMapper userMapperMock;

	@Before
	public void setUp() {
		Mockito.reset(teamRepositoryMock);
		Mockito.reset(matchRepositoryMock);
		Mockito.reset(matchMapperMock);

		t1 = new Team();
		t1.setCode("T1");
		t1.setName("Team1");

		t2 = new Team();
		t2.setCode("T2");
		t2.setName("Team2");

		Date dt = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, 7);
		dt = c.getTime();

		Stadium s1 = new Stadium();
		s1.setId(1L);
		s1.setName("Stadium1");
		s1.setCity("City");

		Set<Bet> bets = new HashSet<>();

		m1 = new Match();
		m1.setId(1L);
		m1.setDatetime(dt);
		m1.setStadium(s1);
		m1.setCategory(1);
		m1.setFinished(false);
		m1.setTeam1(t1);
		m1.setTeam2(t2);
		m1.setBets(bets);

		m1copy = new Match();
		m1copy.setId(m1.getId());
		m1copy.setDatetime((Date) m1.getDatetime().clone());
		m1copy.setStadium(m1.getStadium());
		m1copy.setCategory(m1.getCategory());
		m1copy.setFinished(m1.isFinished());
		m1copy.setTeam1(m1.getTeam1());
		m1copy.setTeam2(m1.getTeam2());
		m1copy.setBets(m1.getBets());

		m1copy2 = new Match();
		m1copy2.setId(m1.getId());
		m1copy2.setDatetime((Date) m1.getDatetime().clone());
		m1copy2.setStadium(m1.getStadium());
		m1copy2.setCategory(m1.getCategory());
		m1copy2.setFinished(m1.isFinished());
		m1copy2.setTeam1(m1.getTeam1());
		m1copy2.setTeam2(m1.getTeam2());
		m1copy2.setBets(m1.getBets());

		StadiumDto s1Dto = new StadiumDto();
		s1Dto.setId(m1.getStadium().getId());
		s1Dto.setName(m1.getStadium().getName());
		s1Dto.setCity(m1.getStadium().getCity());
		TeamDto t1Dto = new TeamDto();
		t1Dto.setName(m1.getTeam1().getName());
		t1Dto.setCode(m1.getTeam1().getCode());
		t1Dto.setGroup(m1.getTeam1().getGroupStage());
		TeamDto t2Dto = new TeamDto();
		t2Dto.setName(m1.getTeam2().getName());
		t2Dto.setCode(m1.getTeam2().getCode());
		t2Dto.setGroup(m1.getTeam2().getGroupStage());
		m1Dto = new MatchDto();
		m1Dto.setId(m1.getId());
		m1Dto.setDatetime((Date) m1.getDatetime().clone());
		m1Dto.setStadium(s1Dto);
		m1Dto.setCategory(m1.getCategory());
		m1Dto.setFinished(m1.isFinished());
		m1Dto.setTeam1(t1Dto);
		m1Dto.setTeam2(t2Dto);

		Mockito.when(matchMapperMock.asMatchDto(Mockito.any())).thenReturn(m1Dto);
		Mockito.when(userRepositoryMock.findAll()).thenReturn(new ArrayList<User>());
	}

	@Test
	public void findAll_ok() {
		// Data preparation
		List<Match> matchesMock = new LinkedList<>();
		matchesMock.add(m1);
		Mockito.when(matchRepositoryMock.findAll(new Sort(Direction.ASC, "datetime"))).thenReturn(matchesMock);

		// Method call
		List<MatchDto> matches = matchService.findAll();

		// Verification
		assertEquals(m1.getId(), matches.get(0).getId());
		Mockito.verify(matchRepositoryMock, Mockito.times(1)).findAll(new Sort(Direction.ASC, "datetime"));
		Mockito.verifyNoMoreInteractions(matchRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = ADMIN_EMAIL, password = ADMIN_PASSWORD, userId = ADMIN_ID, isAdmin = ADMIN_IS_ADMIN)
	public void update_ok() {
		// Data preparation
		MatchChangeDto dto = new MatchChangeDto();
		dto.setFinished(true);
		m1copy2.setFinished(true);
		m1Dto.setFinished(true);
		Mockito.when(matchRepositoryMock.getOne(m1.getId())).thenReturn(m1copy);
		Mockito.when(matchRepositoryMock.save(Mockito.any())).thenReturn(m1copy2);
		Mockito.when(teamRepositoryMock.getOne(t1.getCode())).thenReturn(t1);
		Mockito.when(teamRepositoryMock.getOne(t2.getCode())).thenReturn(t2);

		// Method call
		UpdateMessage message = matchService.update(m1.getId(), dto);
		MatchDto res = (MatchDto) message.getObject();

		// Verification
		assertFalse(res.isFinished() == m1.isFinished());
		Mockito.verify(matchRepositoryMock, Mockito.times(1)).getOne(m1.getId());
		Mockito.verify(matchRepositoryMock, Mockito.times(1)).save(Mockito.any());
		Mockito.verifyNoMoreInteractions(matchRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = ADMIN_EMAIL, password = ADMIN_PASSWORD, userId = ADMIN_ID, isAdmin = ADMIN_IS_ADMIN)
	public void update_idDoesNotExist() {
		// Data preparation
		MatchChangeDto dto = new MatchChangeDto();
		Mockito.when(teamRepositoryMock.getOne(Mockito.any())).thenReturn(null);

		// Method call
		try {
			matchService.update(1L, dto);
			fail("NoSuchElementException was expected");
		} catch (NoSuchElementException e) {
		}

		// Verification
		Mockito.verify(matchRepositoryMock, Mockito.times(1)).getOne(1L);
		Mockito.verifyNoMoreInteractions(matchRepositoryMock);
		Mockito.verifyZeroInteractions(teamRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = ADMIN_EMAIL, password = ADMIN_PASSWORD, userId = ADMIN_ID, isAdmin = ADMIN_IS_ADMIN)
	public void update_negativeScore() {
		// Data preparation
		MatchChangeDto dto = new MatchChangeDto();
		dto.setTeam1Score(-1);
		Mockito.when(matchRepositoryMock.getOne(m1.getId())).thenReturn(m1copy);

		// Method call
		try {
			matchService.update(m1.getId(), dto);
			fail("IllegalArgumentException was expected");
		} catch (IllegalArgumentException e) {
		}

		// Verification
		Mockito.verify(matchRepositoryMock, Mockito.times(1)).getOne(m1.getId());
		Mockito.verifyNoMoreInteractions(matchRepositoryMock);
		Mockito.verifyZeroInteractions(teamRepositoryMock);
	}
}
