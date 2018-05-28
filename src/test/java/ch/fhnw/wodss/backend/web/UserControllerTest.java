package ch.fhnw.wodss.backend.web;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.fhnw.wodss.backend.BackendApplication;
import ch.fhnw.wodss.backend.businesslogic.UserService;
import ch.fhnw.wodss.backend.dto.UserCreateDto;
import ch.fhnw.wodss.backend.dto.UserDataChangeDto;
import ch.fhnw.wodss.backend.dto.UserPasswordChangeDto;
import ch.fhnw.wodss.backend.dto.UserPasswordResetDto;
import ch.fhnw.wodss.backend.dto.UserPasswordResetRequestDto;
import ch.fhnw.wodss.backend.dto.UserPrivateDto;
import ch.fhnw.wodss.backend.dto.UserPublicDto;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { BackendApplication.class })
@WebAppConfiguration
public class UserControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private UserService userServiceMock;

	private MockMvc mockMvc;

	private HttpMessageConverter<Object> mappingJackson2HttpMessageConverter;

	private final static String PATH = "/api/users/";

	@SuppressWarnings("unchecked")
	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {
		mappingJackson2HttpMessageConverter = (HttpMessageConverter<Object>) Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().orElse(null);
		assertNotNull("the JSON message converter must not be null", mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		Mockito.reset(userServiceMock);
	}

	@Test
	public void findAll_Ok() throws IOException, Exception {
		Mockito.when(userServiceMock.findAll()).thenReturn(new LinkedList<UserPublicDto>());

		mockMvc.perform(get(PATH)).andExpect(status().isOk());

		Mockito.verify(userServiceMock, Mockito.times(1)).findAll();
	}

	@Test
	public void create_Ok() throws IOException, Exception {
		Mockito.when(userServiceMock.create(Mockito.any(), Mockito.any())).thenReturn(new UpdateMessage(null, null));

		mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(json(new UserCreateDto())))
				.andExpect(status().isCreated());

		Mockito.verify(userServiceMock, Mockito.times(1)).create(Mockito.any(), Mockito.any());
	}

	@Test
	public void create_IllegalArgument() throws IOException, Exception {
		Mockito.when(userServiceMock.create(Mockito.any(), Mockito.any())).thenThrow(new IllegalArgumentException());

		mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(json(new UserCreateDto())))
				.andExpect(status().isPreconditionFailed());

		Mockito.verify(userServiceMock, Mockito.times(1)).create(Mockito.any(), Mockito.any());
	}

	@Test
	public void create_IllegalState() throws IOException, Exception {
		Mockito.when(userServiceMock.create(Mockito.any(), Mockito.any())).thenThrow(new IllegalStateException());

		mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(json(new UserCreateDto())))
				.andExpect(status().isBadRequest());

		Mockito.verify(userServiceMock, Mockito.times(1)).create(Mockito.any(), Mockito.any());
	}

	@Test
	public void getOne_Ok() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(userServiceMock.getOne(id)).thenReturn(new UserPrivateDto());

		mockMvc.perform(get(PATH + id)).andExpect(status().isOk());

		Mockito.verify(userServiceMock, Mockito.times(1)).getOne(id);
	}

	@Test
	public void getOne_NoSuchElement() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(userServiceMock.getOne(id)).thenThrow(new NoSuchElementException());

		mockMvc.perform(get(PATH + id)).andExpect(status().isNotFound());

		Mockito.verify(userServiceMock, Mockito.times(1)).getOne(id);
	}

	@Test
	public void getOne_IllegalAccess() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(userServiceMock.getOne(id)).thenThrow(new IllegalAccessException());

		mockMvc.perform(get(PATH + id)).andExpect(status().isUnauthorized());

		Mockito.verify(userServiceMock, Mockito.times(1)).getOne(id);
	}

	@Test
	public void update_Ok() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(userServiceMock.updateData(Mockito.eq(id), Mockito.any()))
				.thenReturn(new UpdateMessage(null, null));

		mockMvc.perform(put(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new UserDataChangeDto())))
				.andExpect(status().isOk());

		Mockito.verify(userServiceMock, Mockito.times(1)).updateData(Mockito.eq(id), Mockito.any());
	}

	@Test
	public void update_IllegalArgument() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(userServiceMock.updateData(Mockito.eq(id), Mockito.any()))
				.thenThrow(new IllegalArgumentException());

		mockMvc.perform(put(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new UserDataChangeDto())))
				.andExpect(status().isPreconditionFailed());

		Mockito.verify(userServiceMock, Mockito.times(1)).updateData(Mockito.eq(id), Mockito.any());
	}

	@Test
	public void update_NoSuchElement() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(userServiceMock.updateData(Mockito.eq(id), Mockito.any())).thenThrow(new NoSuchElementException());

		mockMvc.perform(put(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new UserDataChangeDto())))
				.andExpect(status().isNotFound());

		Mockito.verify(userServiceMock, Mockito.times(1)).updateData(Mockito.eq(id), Mockito.any());
	}

	@Test
	public void update_IllegalAccess() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(userServiceMock.updateData(Mockito.eq(id), Mockito.any())).thenThrow(new IllegalAccessException());

		mockMvc.perform(put(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new UserDataChangeDto())))
				.andExpect(status().isUnauthorized());

		Mockito.verify(userServiceMock, Mockito.times(1)).updateData(Mockito.eq(id), Mockito.any());
	}

	@Test
	public void updatePassword_Ok() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(userServiceMock.updatePassword(Mockito.eq(id), Mockito.any())).thenReturn(new UserPrivateDto());

		mockMvc.perform(
				post(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new UserPasswordChangeDto())))
				.andExpect(status().isOk());

		Mockito.verify(userServiceMock, Mockito.times(1)).updatePassword(Mockito.eq(id), Mockito.any());
	}

	@Test
	public void updatePassword_IllegalArgument() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(userServiceMock.updatePassword(Mockito.eq(id), Mockito.any()))
				.thenThrow(new IllegalArgumentException());

		mockMvc.perform(
				post(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new UserPasswordChangeDto())))
				.andExpect(status().isPreconditionFailed());

		Mockito.verify(userServiceMock, Mockito.times(1)).updatePassword(Mockito.eq(id), Mockito.any());
	}

	@Test
	public void updatePassword_NoSuchElement() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(userServiceMock.updatePassword(Mockito.eq(id), Mockito.any()))
				.thenThrow(new NoSuchElementException());

		mockMvc.perform(
				post(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new UserPasswordChangeDto())))
				.andExpect(status().isNotFound());

		Mockito.verify(userServiceMock, Mockito.times(1)).updatePassword(Mockito.eq(id), Mockito.any());
	}

	@Test
	public void updatePassword_IllegalState() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(userServiceMock.updatePassword(Mockito.eq(id), Mockito.any()))
				.thenThrow(new IllegalStateException());

		mockMvc.perform(
				post(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new UserPasswordChangeDto())))
				.andExpect(status().isBadRequest());

		Mockito.verify(userServiceMock, Mockito.times(1)).updatePassword(Mockito.eq(id), Mockito.any());
	}

	@Test
	public void updatePassword_IllegalAccess() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(userServiceMock.updatePassword(Mockito.eq(id), Mockito.any()))
				.thenThrow(new IllegalAccessException());

		mockMvc.perform(
				post(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new UserPasswordChangeDto())))
				.andExpect(status().isUnauthorized());

		Mockito.verify(userServiceMock, Mockito.times(1)).updatePassword(Mockito.eq(id), Mockito.any());
	}

	@Test
	public void resetPasswordRequest_Ok() throws IOException, Exception {
		mockMvc.perform(post(PATH + "/passwordreset/request").contentType(MediaType.APPLICATION_JSON)
				.content(json(new UserPasswordResetRequestDto()))).andExpect(status().isOk());

		Mockito.verify(userServiceMock, Mockito.times(1)).resetPasswordRequest(Mockito.any(), Mockito.any());
	}

	@Test
	public void resetPasswordRequest_IllegalArgument() throws IOException, Exception {
		Mockito.doThrow(new IllegalArgumentException()).when(userServiceMock).resetPasswordRequest(Mockito.any(),
				Mockito.any());

		mockMvc.perform(post(PATH + "/passwordreset/request").contentType(MediaType.APPLICATION_JSON)
				.content(json(new UserPasswordResetRequestDto()))).andExpect(status().isOk());

		Mockito.verify(userServiceMock, Mockito.times(1)).resetPasswordRequest(Mockito.any(), Mockito.any());
	}

	@Test
	public void resetPasswordRequest_NoSuchElement() throws IOException, Exception {
		Mockito.doThrow(new NoSuchElementException()).when(userServiceMock).resetPasswordRequest(Mockito.any(),
				Mockito.any());

		mockMvc.perform(post(PATH + "/passwordreset/request").contentType(MediaType.APPLICATION_JSON)
				.content(json(new UserPasswordResetRequestDto()))).andExpect(status().isOk());

		Mockito.verify(userServiceMock, Mockito.times(1)).resetPasswordRequest(Mockito.any(), Mockito.any());
	}

	@Test
	public void resetPassword_Ok() throws IOException, Exception {
		mockMvc.perform(post(PATH + "/passwordreset").contentType(MediaType.APPLICATION_JSON)
				.content(json(new UserPasswordResetDto()))).andExpect(status().isOk());

		Mockito.verify(userServiceMock, Mockito.times(1)).resetPassword(Mockito.any(), Mockito.any());
	}

	@Test
	public void resetPassword_IllegalArgument() throws IOException, Exception {
		Mockito.doThrow(new IllegalArgumentException()).when(userServiceMock).resetPassword(Mockito.any(),
				Mockito.any());

		mockMvc.perform(post(PATH + "/passwordreset").contentType(MediaType.APPLICATION_JSON)
				.content(json(new UserPasswordResetDto()))).andExpect(status().isBadRequest());

		Mockito.verify(userServiceMock, Mockito.times(1)).resetPassword(Mockito.any(), Mockito.any());
	}

	@Test
	public void resetPassword_NoSuchElement() throws IOException, Exception {
		Mockito.doThrow(new NoSuchElementException()).when(userServiceMock).resetPassword(Mockito.any(), Mockito.any());

		mockMvc.perform(post(PATH + "/passwordreset").contentType(MediaType.APPLICATION_JSON)
				.content(json(new UserPasswordResetDto()))).andExpect(status().isBadRequest());

		Mockito.verify(userServiceMock, Mockito.times(1)).resetPassword(Mockito.any(), Mockito.any());
	}

	@Test
	public void resetPassword_IllegalState() throws IOException, Exception {
		Mockito.doThrow(new IllegalStateException()).when(userServiceMock).resetPassword(Mockito.any(), Mockito.any());

		mockMvc.perform(post(PATH + "/passwordreset").contentType(MediaType.APPLICATION_JSON)
				.content(json(new UserPasswordResetDto()))).andExpect(status().isBadRequest());

		Mockito.verify(userServiceMock, Mockito.times(1)).resetPassword(Mockito.any(), Mockito.any());
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}
