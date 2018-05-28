package ch.fhnw.wodss.backend.businesslogic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import ch.fhnw.wodss.backend.captcha.CaptchaService;
import ch.fhnw.wodss.backend.config.SpringBootConfig;
import ch.fhnw.wodss.backend.config.WithMockCustomUser;
import ch.fhnw.wodss.backend.domain.User;
import ch.fhnw.wodss.backend.dto.UserCreateDto;
import ch.fhnw.wodss.backend.dto.UserDataChangeDto;
import ch.fhnw.wodss.backend.dto.UserPasswordChangeDto;
import ch.fhnw.wodss.backend.dto.UserPrivateDto;
import ch.fhnw.wodss.backend.dto.UserPublicDto;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;
import ch.fhnw.wodss.backend.persistence.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootConfig.class })
public class UserServiceTest {
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

	private User u1;
	private User u2;
	private UserCreateDto u1CreateDto;
	private UserCreateDto u2CreateDto;
	private User u1copy;
	private User u2copy;

	@MockBean
	private UserRepository userRepositoryMock;

	@MockBean
	private CaptchaService captchaServiceMock;

	@Autowired
	@InjectMocks
	private UserService userService;

	@Autowired
	private PasswordEncoder encoder;

	@Before
	public void setUp() {
		Mockito.reset(userRepositoryMock);

		u1 = new User();
		u1.setId(U1_ID);
		u1.setName(U1_NAME);
		u1.setEmail(U1_EMAIL);
		u1.setPassword(encoder.encode(U1_PASSWORD));
		u1.setAdmin(U1_IS_ADMIN);

		u1CreateDto = new UserCreateDto();
		u1CreateDto.setName(u1.getName());
		u1CreateDto.setEmail(u1.getEmail());
		u1CreateDto.setPassword(u1.getPassword());
		u1CreateDto.setReCaptchaResponse("Valid ReCaptcha");

		u2 = new User();
		u2.setId(U2_ID);
		u2.setName(U2_NAME);
		u2.setEmail(U2_EMAIL);
		u2.setPassword(encoder.encode(U2_PASSWORD));
		u2.setAdmin(U2_IS_ADMIN);

		u2CreateDto = new UserCreateDto();
		u2CreateDto.setName(u2.getName());
		u2CreateDto.setEmail(u2.getEmail());
		u2CreateDto.setPassword(u2.getPassword());
		u2CreateDto.setReCaptchaResponse("Valid ReCaptcha");

		u1copy = new User();
		u1copy.setId(U1_ID);
		u1copy.setName(U1_NAME);
		u1copy.setEmail(U1_EMAIL);
		u1copy.setPassword(encoder.encode(U1_PASSWORD));
		u1copy.setAdmin(U1_IS_ADMIN);

		u2copy = new User();
		u2copy.setId(U2_ID);
		u2copy.setName(U2_NAME);
		u2copy.setEmail(U2_EMAIL);
		u2copy.setPassword(encoder.encode(U2_PASSWORD));
		u2copy.setAdmin(U2_IS_ADMIN);
		
		Mockito.when(captchaServiceMock.validate(Mockito.anyString())).thenReturn(true);
	}

	@Test
	public void findAll_ok() {
		// Data preparation
		List<User> users = new LinkedList<>();
		users.add(u1);
		users.add(u2);
		Mockito.when(userRepositoryMock.findAll(new Sort(Direction.ASC, "id"))).thenReturn(users);

		// Method call
		List<UserPublicDto> pUserDtos = userService.findAll();

		// Verification
		assertNotNull(pUserDtos);
		assertEquals(2, pUserDtos.size());
		int itemsToFind = 2;
		for (UserPublicDto dto : pUserDtos) {
			if (dto.getId() == U1_ID)
				itemsToFind--;
			if (dto.getId() == U2_ID)
				itemsToFind--;
		}
		assertEquals(0, itemsToFind);
		Mockito.verify(userRepositoryMock, Mockito.times(1)).findAll(new Sort(Direction.ASC, "id"));
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	public void create_ok() {
		// Data preparation
		Mockito.when(userRepositoryMock.save(Mockito.any())).thenReturn(u1copy);

		// Method call
		UpdateMessage message = userService.create(u1CreateDto, "");

		UserPrivateDto dto = (UserPrivateDto) message.getObject();

		// Verification
		assertTrue(compareUserPrivateDtoToUser(dto, u1));
		Mockito.verify(userRepositoryMock, Mockito.times(1)).existsByEmail(u1.getEmail());
		Mockito.verify(userRepositoryMock, Mockito.times(1)).save(Mockito.any());
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_notAnEmail() {
		// Data preparation
		u1CreateDto.setEmail("r1jhf8723j87frcj23894");

		// Method call
		userService.create(u1CreateDto, "");

		// Should throw exception
		// No interaction with userRepository
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_emailNull() {
		// Data preparation
		u1CreateDto.setEmail(null);

		// Method call
		userService.create(u1CreateDto, "");

		// Should throw exception
		// No interaction with userRepository
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_passwordTooShort() {
		// Data preparation
		u1CreateDto.setPassword("1234567");

		// Method call
		userService.create(u1CreateDto,"");

		// Should throw exception
		// No interaction with userRepository
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_passwordNull() {
		// Data preparation
		u1CreateDto.setPassword(null);

		// Method call
		userService.create(u1CreateDto, "");

		// Should throw exception
		// No interaction with userRepository
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_nameTooShort() {
		// Data preparation
		u1CreateDto.setName("1");

		// Method call
		userService.create(u1CreateDto, "");

		// Should throw exception
		// No interaction with userRepository
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_nameTooLong() {
		// Data preparation
		u1CreateDto.setName(
				"rh38fcr91cj243rfj34f93j381f3c9jdn8f1jm9dj29m4jnr82fcnh4r9f2n43nvne5zh6eju5rk7iju6hz5gt4fr3de3fr3gt5z36we5gwt4h5z65gvw6h5twg4g35tg4h56z36zhz5ge");

		// Method call
		userService.create(u1CreateDto, "");

		// Should throw exception
		// No interaction with userRepository
	}

	@Test()
	public void create_emailAlreadyExists() {
		// Data preparation
		Mockito.when(userRepositoryMock.existsByEmail(U1_EMAIL)).thenReturn(true);
		u2CreateDto.setEmail(U1_EMAIL);

		// Method call
		try {
			userService.create(u2CreateDto, "");
		} catch (IllegalStateException e) {
		}

		// Verification
		Mockito.verify(userRepositoryMock, Mockito.times(1)).existsByEmail(U1_EMAIL);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = U1_EMAIL, password = U1_PASSWORD, userId = U1_ID)
	public void getOne_ok() {
		// Data preparation
		Mockito.when(userRepositoryMock.getOne(U1_ID)).thenReturn(u1copy);

		// Method call
		UserPrivateDto dto = null;
		try {
			dto = userService.getOne(U1_ID);
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException not expected");
		}

		// Verification
		assertTrue(compareUserPrivateDtoToUser(dto, u1));
		Mockito.verify(userRepositoryMock, Mockito.times(2)).getOne(U1_ID);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = U1_EMAIL, password = U1_PASSWORD, userId = U1_ID)
	public void getOne_idDoesNotExist() {
		// Data preparation
		Mockito.when(userRepositoryMock.getOne(1000L)).thenReturn(null);

		// Method call
		try {
			userService.getOne(1000L);
		} catch (NoSuchElementException e) {
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException not expected");
		}

		// Verification
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(1000L);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = U2_EMAIL, password = U2_PASSWORD, userId = U2_ID)
	public void getOne_illegalAccess() {
		// Data preparation
		Mockito.when(userRepositoryMock.getOne(U1_ID)).thenReturn(u1copy);
		Mockito.when(userRepositoryMock.getOne(U2_ID)).thenReturn(u2copy);

		// Method call
		try {
			userService.getOne(U1_ID);
			fail("IllegalAccessException expected");
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException not expected");
		} catch (IllegalAccessException e) {
		}

		// Verification
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(U1_ID);
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(U2_ID);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = U1_EMAIL, password = U1_PASSWORD, userId = U1_ID)
	public void updateData_ok() {
		// Data preparation
		Mockito.when(userRepositoryMock.getOne(U1_ID)).thenReturn(u1);
		UserDataChangeDto uDto = new UserDataChangeDto();
		uDto.setName(U1_NAME + "abc");

		// Method call
		UserPrivateDto dto = null;
		try {
			UpdateMessage message = userService.updateData(u1.getId(), uDto);
			dto = (UserPrivateDto) message.getObject();
		} catch (IllegalArgumentException e) {
			fail("IllegalArgumentException not expected");
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException not expected");
		}

		// Verification
		assertNotEquals(dto.getName(), U1_NAME);
		Mockito.verify(userRepositoryMock, Mockito.times(2)).getOne(U1_ID);
		Mockito.verify(userRepositoryMock, Mockito.times(1)).save(Mockito.any());
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = U1_EMAIL, password = U1_PASSWORD, userId = U1_ID)
	public void updateData_nameTooShort() {
		// Data preparation
		Mockito.when(userRepositoryMock.getOne(U1_ID)).thenReturn(u1copy);
		UserDataChangeDto uDto = new UserDataChangeDto();
		uDto.setName("1");

		// Method call & Verification
		updateIllegalData(uDto);
	}

	@Test
	@WithMockCustomUser(username = U1_EMAIL, password = U1_PASSWORD, userId = U1_ID)
	public void updateData_nameTooLong() {
		// Data preparation
		Mockito.when(userRepositoryMock.getOne(U1_ID)).thenReturn(u1copy);
		UserDataChangeDto uDto = new UserDataChangeDto();
		uDto.setName("jr244j2tvug598fu2r298cr9d24njrfn82jmf29rj2fhr5dhj19f2nr81nfh8rhdd2fbfu8fnr2");

		// Method call & Verification
		updateIllegalData(uDto);
	}

	@Test
	@WithMockCustomUser(username = U1_EMAIL, password = U1_PASSWORD, userId = U1_ID)
	public void updateData_nameNull() {
		// Data preparation
		Mockito.when(userRepositoryMock.getOne(U1_ID)).thenReturn(u1copy);
		UserDataChangeDto uDto = new UserDataChangeDto();
		uDto.setName(null);

		// Method call & Verification
		updateIllegalData(uDto);
	}

	private void updateIllegalData(UserDataChangeDto uDto) {
		// Method call
		UserPrivateDto dto = null;
		try {
			UpdateMessage message = userService.updateData(u1.getId(), uDto);
			dto = (UserPrivateDto) message.getObject();
		} catch (IllegalArgumentException e) {
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException not expected");
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException not expected");
		}

		// Verification
		assertFalse(compareUserPrivateDtoToUser(dto, u1));
		Mockito.verifyZeroInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = U1_EMAIL, password = U1_PASSWORD, userId = U1_ID)
	public void updateData_idDoesNotExist() {
		// Data preparation
		Mockito.when(userRepositoryMock.getOne(U1_ID)).thenReturn(null);
		UserDataChangeDto uDto = new UserDataChangeDto();
		uDto.setName(U1_NAME);

		// Method call
		try {
			userService.updateData(U1_ID, uDto);
		} catch (IllegalArgumentException e) {
			fail("IllegalArgumentException not expected");
		} catch (NoSuchElementException e) {
		} catch (IllegalAccessException e) {
			fail("IllegalAccessException not expected");
		}

		// Verification
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(U1_ID);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = U2_EMAIL, password = U2_PASSWORD, userId = U2_ID)
	public void updateData_illegalAccess() {
		// Data preparation
		Mockito.when(userRepositoryMock.getOne(U1_ID)).thenReturn(u1copy);
		Mockito.when(userRepositoryMock.getOne(U2_ID)).thenReturn(u2copy);
		UserDataChangeDto uDto = new UserDataChangeDto();
		uDto.setName(U1_NAME + "abc");

		// Method call
		try {
			userService.updateData(U1_ID, uDto);
			fail("IllegalAccessException expected");
		} catch (IllegalArgumentException e) {
			fail("IllegalArgumentException not expected");
		} catch (NoSuchElementException e) {
			fail("NoSuchElementException not expected");
		} catch (IllegalAccessException e) {
		}

		// Verification
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(U1_ID);
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(U2_ID);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = U1_EMAIL, password = U1_PASSWORD, userId = U1_ID)
	public void updatePassword_ok() {
		// Data preparation
		Mockito.when(userRepositoryMock.getOne(U1_ID)).thenReturn(u1copy);
		String newPassword = U1_PASSWORD + "1234";
		UserPasswordChangeDto pDto = new UserPasswordChangeDto();
		pDto.setOldPassword(U1_PASSWORD);
		pDto.setNewPassword(newPassword);

		// Method call
		try {
			userService.updatePassword(U1_ID, pDto);
		} catch (IllegalArgumentException | NoSuchElementException | IllegalStateException | IllegalAccessException e) {
			fail("Unexpected exception occured");
		}

		// Verification
		Mockito.verify(userRepositoryMock, Mockito.times(2)).getOne(U1_ID);
		Mockito.verify(userRepositoryMock, Mockito.times(1)).save(Mockito.any());
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = U1_EMAIL, password = U1_PASSWORD, userId = U1_ID)
	public void updatePassword_newPwTooShort() {
		// Data preparation
		String newPassword = "1";
		UserPasswordChangeDto pDto = new UserPasswordChangeDto();
		pDto.setOldPassword(U1_PASSWORD);
		pDto.setNewPassword(newPassword);

		// Method call
		try {
			userService.updatePassword(u1.getId(), pDto);
			fail("IllegalArgumentException expected");
		} catch (NoSuchElementException | IllegalStateException | IllegalAccessException e) {
			fail("Unexpected exception occured");
		} catch (IllegalArgumentException e) {
		}

		// Verification
		Mockito.verifyZeroInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = U1_EMAIL, password = U1_PASSWORD, userId = U1_ID)
	public void updatePassword_newPwNull() {
		// Data preparation
		Mockito.when(userRepositoryMock.getOne(U1_ID)).thenReturn(u1copy);
		String newPassword = null;
		UserPasswordChangeDto pDto = new UserPasswordChangeDto();
		pDto.setOldPassword(U1_PASSWORD);
		pDto.setNewPassword(newPassword);

		// Method call
		try {
			userService.updatePassword(u1.getId(), pDto);
			fail("IllegalArgumentException expected");
		} catch (NoSuchElementException | IllegalStateException | IllegalAccessException e) {
			fail("Unexpected exception occured");
		} catch (IllegalArgumentException e) {
		}

		// Verification
		Mockito.verifyZeroInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = U1_EMAIL, password = U1_PASSWORD, userId = U1_ID)
	public void updatePassword_idDoesNotExist() {
		// Data preparation
		Mockito.when(userRepositoryMock.getOne(U1_ID)).thenReturn(null);
		String newPassword = U1_PASSWORD + "1234";
		UserPasswordChangeDto pDto = new UserPasswordChangeDto();
		pDto.setOldPassword(U1_PASSWORD);
		pDto.setNewPassword(newPassword);

		// Method call
		try {
			userService.updatePassword(u1.getId(), pDto);
			fail("NoSuchElementException expected");
		} catch (IllegalArgumentException | IllegalStateException | IllegalAccessException e) {
			fail("Unexpected exception occured");
		} catch (NoSuchElementException e) {
		}

		// Verification
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(U1_ID);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = U1_EMAIL, password = U1_PASSWORD, userId = U1_ID)
	public void updatePassword_oldPwWrong() {
		// Data preparation
		Mockito.when(userRepositoryMock.getOne(U1_ID)).thenReturn(u1copy);
		String newPassword = U1_PASSWORD + "1234";
		UserPasswordChangeDto pDto = new UserPasswordChangeDto();
		pDto.setOldPassword(U1_PASSWORD + "abc");
		pDto.setNewPassword(newPassword);

		// Method call
		try {
			userService.updatePassword(U1_ID, pDto);
			fail("IllegalStateException expected");
		} catch (IllegalArgumentException | NoSuchElementException | IllegalAccessException e) {
			fail("Unexpected exception occured");
		} catch (IllegalStateException e) {
		}

		// Verification
		Mockito.verify(userRepositoryMock, Mockito.times(2)).getOne(U1_ID);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	@Test
	@WithMockCustomUser(username = U2_EMAIL, password = U2_PASSWORD, userId = U2_ID)
	public void updatePassword_illegalAccess() {
		// Data preparation
		Mockito.when(userRepositoryMock.getOne(U1_ID)).thenReturn(u1copy);
		Mockito.when(userRepositoryMock.getOne(U2_ID)).thenReturn(u2copy);
		String newPassword = U1_PASSWORD + "1234";
		UserPasswordChangeDto pDto = new UserPasswordChangeDto();
		pDto.setOldPassword(U1_PASSWORD);
		pDto.setNewPassword(newPassword);

		// Method call
		try {
			userService.updatePassword(U1_ID, pDto);
			fail("IllegalAccessException expected");
		} catch (IllegalArgumentException | NoSuchElementException | IllegalStateException e) {
			fail("Unexpected exception occured");
		} catch (IllegalAccessException e) {
		}

		// Verification
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(U1_ID);
		Mockito.verify(userRepositoryMock, Mockito.times(1)).getOne(U2_ID);
		Mockito.verifyNoMoreInteractions(userRepositoryMock);
	}

	private static boolean compareUserPrivateDtoToUser(UserPrivateDto dto, User user) {
		if (dto == null && user == null)
			return true;
		if (dto == null || user == null)
			return false;
		return dto.getId() == user.getId() && dto.getEmail().equals(user.getEmail())
				&& dto.getName().equals(user.getName()) && dto.getPoints() == user.getPoints();
	}
}
