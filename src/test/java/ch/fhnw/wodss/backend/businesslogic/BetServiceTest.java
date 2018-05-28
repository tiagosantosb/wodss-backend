package ch.fhnw.wodss.backend.businesslogic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import ch.fhnw.wodss.backend.config.SpringBootConfig;
import ch.fhnw.wodss.backend.config.WithMockCustomUser;
import ch.fhnw.wodss.backend.domain.Bet;
import ch.fhnw.wodss.backend.domain.Match;
import ch.fhnw.wodss.backend.domain.Stadium;
import ch.fhnw.wodss.backend.domain.User;
import ch.fhnw.wodss.backend.dto.BetChangeDto;
import ch.fhnw.wodss.backend.dto.BetDto;
import ch.fhnw.wodss.backend.mapping.MatchMapper;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;
import ch.fhnw.wodss.backend.persistence.BetRepository;
import ch.fhnw.wodss.backend.persistence.MatchRepository;
import ch.fhnw.wodss.backend.persistence.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootConfig.class })
@WithMockCustomUser(username = BetServiceTest.U1_EMAIL, password = BetServiceTest.U1_PASSWORD, userId = BetServiceTest.U1_ID)
public class BetServiceTest {
	public final static long B1_ID = 1L;

	public final static long U1_ID = 1L;
	public final static String U1_NAME = "user1";
	public final static String U1_EMAIL = "u1@test.ch";
	public final static String U1_PASSWORD = "12345678";
	public final static boolean U1_IS_ADMIN = false;

	public final static long U2_ID = 2L;
	public final static String U2_NAME = "user2";
	public final static String U2_EMAIL = "u2@test.ch";
	public final static String U2_PASSWORD = "12345678";
	public final static boolean U2_IS_ADMIN = false;

	public final static long M1_ID = 1L;

	public final static long S1_ID = 1L;
	public final static String S1_NAME = "stadium1";
	public final static String S1_CITY = "city";

	private Bet b1;
	private User u1;
	private List<Bet> u1bets;
	private Match m1;
	private Match m1copy;
	private Bet b1copy;
	private User u2;
	private User u1copy;
	private User u2copy;
	private Stadium s1;

	@MockBean
	private BetRepository betRepositoryMock;

	@MockBean
	private UserRepository userRepositoryMock;

	@MockBean
	private MatchRepository matchRepositoryMock;
	
	@MockBean
    private MatchMapper matchMapperMock;

	@Autowired
	@InjectMocks
	private BetService betService;

	@Autowired
	private PasswordEncoder encoder;

	@Before
	public void setUp() throws ParseException {
		Mockito.reset(betRepositoryMock);
		Mockito.reset(userRepositoryMock);
		Mockito.reset(matchRepositoryMock);
		Mockito.reset(matchMapperMock);

		u1 = new User();
		u1.setId(U1_ID);
		u1.setName(U1_NAME);
		u1.setEmail(U1_EMAIL);
		u1.setPassword(encoder.encode(U1_PASSWORD));
		u1.setAdmin(U1_IS_ADMIN);
		
		u1copy = new User();
		u1copy.setId(U1_ID);
		u1copy.setName(U1_NAME);
		u1copy.setEmail(U1_EMAIL);
		u1copy.setPassword(encoder.encode(U1_PASSWORD));
		u1copy.setAdmin(U1_IS_ADMIN);

		u2 = new User();
		u2.setId(U2_ID);
		u2.setName(U2_NAME);
		u2.setEmail(U2_EMAIL);
		u2.setPassword(encoder.encode(U2_PASSWORD));
		u2.setAdmin(U2_IS_ADMIN);

		u2copy = new User();
		u2copy.setId(U2_ID);
		u2copy.setName(U2_NAME);
		u2copy.setEmail(U2_EMAIL);
		u2copy.setPassword(encoder.encode(U2_PASSWORD));
		u2copy.setAdmin(U2_IS_ADMIN);
		
		Set<Bet> m1bets = new HashSet<>();
		m1bets.add(b1);
		
		Date dt = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, 7);
		dt = c.getTime();

		s1 = new Stadium();
		s1.setId(S1_ID);
		s1.setName(S1_NAME);
		s1.setCity(S1_CITY);
		
		m1 = new Match();
		m1.setId(M1_ID);
		m1.setDatetime(dt);
		m1.setStadium(s1);
		m1.setCategory(0);
		m1.setFinished(false);
		m1.setTeam1(null);
		m1.setTeam2(null);
		m1.setPlaceholderGroup1("p");
		m1.setPlaceholderGroup2("p");
		m1.setTeam1Score(0);
		m1.setTeam2Score(0);
		m1.setBets(m1bets);

		m1copy = new Match();
		m1copy.setId(M1_ID);
		m1copy.setDatetime(dt);
		m1copy.setStadium(s1);
		m1copy.setCategory(0);
		m1copy.setFinished(false);
		m1copy.setTeam1(null);
		m1copy.setTeam2(null);
		m1copy.setPlaceholderGroup1("p");
		m1copy.setPlaceholderGroup2("p");
		m1copy.setTeam1Score(0);
		m1copy.setTeam2Score(0);
		m1copy.setBets(m1bets);

		b1 = new Bet();
		b1.setId(B1_ID);
		b1.setUser(u1);
		b1.setMatch(m1);

		b1copy = new Bet();
		b1copy.setId(B1_ID);
		b1copy.setUser(u1);
		b1copy.setMatch(m1);
		
		u1bets = new LinkedList<>();
		u1bets.add(b1copy);
		
		Mockito.when(betRepositoryMock.findByUserId(U1_ID)).thenReturn(u1bets);
		Mockito.when(betRepositoryMock.getOne(B1_ID)).thenReturn(b1copy);

		Mockito.when(userRepositoryMock.getOne(U1_ID)).thenReturn(u1copy);
		Mockito.when(userRepositoryMock.getOne(U2_ID)).thenReturn(u2copy);

		Mockito.when(matchRepositoryMock.getOne(M1_ID)).thenReturn(m1copy);
		Mockito.when(matchRepositoryMock.findById(M1_ID)).thenReturn(Optional.of(m1copy));
		
		Mockito.when(matchMapperMock.asMatchDto(Mockito.any())).thenReturn(null);
	}

	@Test
	public void findAll_ok() {
		// Method call
		List<BetDto> res = betService.findAll();

		// Verification
		assertTrue(res.size() == 1);
		int count = 0;
		for (BetDto dto : res) {
			if (dto.getId() == B1_ID) {
				count++;
			}
		}
		assertEquals(u1bets.size(), count);
		Mockito.verify(betRepositoryMock, Mockito.times(1)).findByUserId(U1_ID);
		Mockito.verifyNoMoreInteractions(betRepositoryMock);
	}

	@Test
	public void create_ok() {
		// Data preparation
		Mockito.reset(betRepositoryMock);
		BetDto newBet = new BetDto();
		newBet.setMatchId(M1_ID);
		newBet.setTeam1Score(0);
		newBet.setTeam2Score(0);
		Mockito.when(betRepositoryMock.save(Mockito.any())).thenReturn(b1copy);

		// Method call
		UpdateMessage message = betService.create(newBet);
		BetDto dto = (BetDto) message.getObject();

		// Verification
		assertTrue(dto.getTeam1Score() == 0);
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(U1_ID);
		Mockito.verify(matchRepositoryMock, Mockito.times(2)).getOne(M1_ID);
		Mockito.verify(betRepositoryMock, Mockito.times(1)).save(Mockito.any());
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
		Mockito.verifyNoMoreInteractions(matchRepositoryMock);
		Mockito.verifyNoMoreInteractions(betRepositoryMock);
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_matchIdNull() {
		// Data preparation
		BetDto newBet = new BetDto();
		newBet.setPoints(0);
		newBet.setTeam1Score(1);
		newBet.setTeam2Score(0);

		// Method call
		betService.create(newBet);

		// Should throw exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_scoreLessThanZero() {
		// Data preparation
		BetDto newBet = new BetDto();
		newBet.setMatchId(M1_ID);
		newBet.setPoints(0);
		newBet.setTeam1Score(-1);
		newBet.setTeam2Score(0);

		// Method call
		betService.create(newBet);

		// Should throw exception
	}

	@Test
	public void update_ok() {
		// Data preparation
		BetChangeDto dto = new BetChangeDto();
		dto.setTeam1Score(0);
		dto.setTeam2Score(5);
		b1copy.setTeam1Score(dto.getTeam1Score());
		b1copy.setTeam2Score(dto.getTeam2Score());
		Mockito.when(betRepositoryMock.save(Mockito.any())).thenReturn(b1copy);

		// Method call
		UpdateMessage message = null;
		BetDto resDto = null;
		try {
			message = betService.update(B1_ID, dto);
			resDto = (BetDto) message.getObject();
		} catch (IllegalArgumentException e) {
			fail("IllegalArgumentException not expected");
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException not expected");
		}

		// Verification
		assertTrue(message != null && resDto != null);
		assertEquals(5, resDto.getTeam2Score());
		Mockito.verify(betRepositoryMock, Mockito.times(1)).getOne(B1_ID);
		Mockito.verify(betRepositoryMock, Mockito.times(1)).save(Mockito.any());
		Mockito.verifyNoMoreInteractions(betRepositoryMock);
		Mockito.verifyZeroInteractions(userRepositoryMock);
		Mockito.verifyZeroInteractions(matchRepositoryMock);
	}

	@Test
	public void update_scoreLessThanZero() {
		// Data preparation
		BetChangeDto dto = new BetChangeDto();
		dto.setTeam1Score(-1);
		dto.setTeam2Score(0);

		// Method call
		try {
			betService.update(B1_ID, dto);
			fail("IllegalArgumentException was expected");
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException not expected");
		} catch (IllegalArgumentException e) {
		}

		// Verification
		Mockito.verifyZeroInteractions(betRepositoryMock);
		Mockito.verifyZeroInteractions(userRepositoryMock);
		Mockito.verifyZeroInteractions(matchRepositoryMock);
	}

	@Test
	public void update_idDoesNotExist() {
		// Data preparation
		BetChangeDto dto = new BetChangeDto();
		dto.setTeam1Score(0);
		dto.setTeam2Score(0);

		// Method call
		try {
			betService.update(B1_ID + 50, dto);
			fail("NoSuchElementException was expected");
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException not expected");
		} catch (NoSuchElementException e) {
		}

		// Verification
		Mockito.verify(betRepositoryMock, Mockito.times(1)).getOne(B1_ID + 50);
		Mockito.verifyNoMoreInteractions(betRepositoryMock);
		Mockito.verifyZeroInteractions(userRepositoryMock);
		Mockito.verifyZeroInteractions(matchRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = U2_EMAIL, password = U2_PASSWORD, userId = U2_ID)
	public void update_wrongUser() {
		// Data preparation
		BetChangeDto dto = new BetChangeDto();
		dto.setTeam1Score(0);
		dto.setTeam2Score(0);

		// Method call
		try {
			betService.update(B1_ID, dto);
			fail("IllegalAccessException was expected");
		} catch (IllegalArgumentException e) {
			fail("IllegalArgumentException not expected");
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException not expected");
		} catch (IllegalAccessException e) {
		}

		// Verification
		Mockito.verify(betRepositoryMock, Mockito.times(1)).getOne(B1_ID);
		Mockito.verifyNoMoreInteractions(betRepositoryMock);
		Mockito.verifyZeroInteractions(userRepositoryMock);
		Mockito.verifyZeroInteractions(matchRepositoryMock);
	}
}
