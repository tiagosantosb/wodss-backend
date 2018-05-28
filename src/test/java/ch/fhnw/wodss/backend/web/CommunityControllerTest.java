package ch.fhnw.wodss.backend.web;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import ch.fhnw.wodss.backend.businesslogic.CommunityService;
import ch.fhnw.wodss.backend.dto.CommunityChangeDto;
import ch.fhnw.wodss.backend.dto.CommunityCreateDto;
import ch.fhnw.wodss.backend.dto.CommunityDataDto;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { BackendApplication.class })
@WebAppConfiguration
public class CommunityControllerTest {
	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private CommunityService communityServiceMock;

	private MockMvc mockMvc;

	private HttpMessageConverter<Object> mappingJackson2HttpMessageConverter;

	private final static String PATH = "/api/communities/";

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
		Mockito.reset(communityServiceMock);
	}

	@Test
	public void findAll_Ok() throws IOException, Exception {
		Mockito.when(communityServiceMock.findAll()).thenReturn(new LinkedList<CommunityDataDto>());

		mockMvc.perform(get(PATH)).andExpect(status().isOk());

		Mockito.verify(communityServiceMock, Mockito.times(1)).findAll();
	}

	@Test
	public void create_Ok() throws IOException, Exception {
		Mockito.when(communityServiceMock.create(Mockito.any())).thenReturn(new UpdateMessage(null, null));

		mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(json(new CommunityCreateDto())))
				.andExpect(status().isCreated());

		Mockito.verify(communityServiceMock, Mockito.times(1)).create(Mockito.any());
	}

	@Test
	public void create_IllegalArgument() throws IOException, Exception {
		Mockito.when(communityServiceMock.create(Mockito.any())).thenThrow(new IllegalArgumentException());

		mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(json(new CommunityCreateDto())))
				.andExpect(status().isPreconditionFailed());

		Mockito.verify(communityServiceMock, Mockito.times(1)).create(Mockito.any());
	}

	@Test
	public void delete_Ok() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(communityServiceMock.delete(id)).thenReturn(new UpdateMessage(null, null));

		mockMvc.perform(delete(PATH + id)).andExpect(status().isOk());

		Mockito.verify(communityServiceMock, Mockito.times(1)).delete(id);
	}

	@Test
	public void delete_NoSuchElement() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(communityServiceMock.delete(id)).thenThrow(new NoSuchElementException());

		mockMvc.perform(delete(PATH + id)).andExpect(status().isNotFound());

		Mockito.verify(communityServiceMock, Mockito.times(1)).delete(id);
	}

	@Test
	public void delete_IllegalAccess() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(communityServiceMock.delete(id)).thenThrow(new IllegalAccessException());

		mockMvc.perform(delete(PATH + id)).andExpect(status().isUnauthorized());

		Mockito.verify(communityServiceMock, Mockito.times(1)).delete(id);
	}

	@Test
	public void createJoinRequest_Ok() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(communityServiceMock.createJoinRequest(id)).thenReturn(new UpdateMessage(null, null));

		mockMvc.perform(post(PATH + id + "/joinrequests")).andExpect(status().isCreated());

		Mockito.verify(communityServiceMock, Mockito.times(1)).createJoinRequest(id);
	}

	@Test
	public void createJoinRequest_NoSuchElement() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(communityServiceMock.createJoinRequest(id)).thenThrow(new NoSuchElementException());

		mockMvc.perform(post(PATH + id + "/joinrequests")).andExpect(status().isNotFound());

		Mockito.verify(communityServiceMock, Mockito.times(1)).createJoinRequest(id);
	}

	@Test
	public void createJoinRequest_IllegalState() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(communityServiceMock.createJoinRequest(id)).thenThrow(new IllegalStateException());

		mockMvc.perform(post(PATH + id + "/joinrequests")).andExpect(status().isBadRequest());

		Mockito.verify(communityServiceMock, Mockito.times(1)).createJoinRequest(id);
	}

	@Test
	public void deleteJoinRequest_Ok() throws IOException, Exception {
		final long communityId = 1L;
		final long joinRequesterId = 1L;
		Mockito.when(communityServiceMock.deleteJoinRequest(communityId, joinRequesterId))
				.thenReturn(new UpdateMessage(null, null));

		mockMvc.perform(delete(PATH + communityId + "/joinrequests/" + joinRequesterId)).andExpect(status().isOk());

		Mockito.verify(communityServiceMock, Mockito.times(1)).deleteJoinRequest(communityId, joinRequesterId);
	}

	@Test
	public void deleteJoinRequest_NoSuchElement() throws IOException, Exception {
		final long communityId = 1L;
		final long joinRequesterId = 1L;
		Mockito.when(communityServiceMock.deleteJoinRequest(communityId, joinRequesterId))
				.thenThrow(new NoSuchElementException());

		mockMvc.perform(delete(PATH + communityId + "/joinrequests/" + joinRequesterId))
				.andExpect(status().isNotFound());

		Mockito.verify(communityServiceMock, Mockito.times(1)).deleteJoinRequest(communityId, joinRequesterId);
	}

	@Test
	public void deleteJoinRequest_IllegalState() throws IOException, Exception {
		final long communityId = 1L;
		final long joinRequesterId = 1L;
		Mockito.when(communityServiceMock.deleteJoinRequest(communityId, joinRequesterId))
				.thenThrow(new IllegalStateException());

		mockMvc.perform(delete(PATH + communityId + "/joinrequests/" + joinRequesterId))
				.andExpect(status().isBadRequest());

		Mockito.verify(communityServiceMock, Mockito.times(1)).deleteJoinRequest(communityId, joinRequesterId);
	}

	@Test
	public void deleteJoinRequest_IllegalAccess() throws IOException, Exception {
		final long communityId = 1L;
		final long joinRequesterId = 1L;
		Mockito.when(communityServiceMock.deleteJoinRequest(communityId, joinRequesterId))
				.thenThrow(new IllegalAccessException());

		mockMvc.perform(delete(PATH + communityId + "/joinrequests/" + joinRequesterId))
				.andExpect(status().isUnauthorized());

		Mockito.verify(communityServiceMock, Mockito.times(1)).deleteJoinRequest(communityId, joinRequesterId);
	}

	@Test
	public void acceptJoinRequest_Ok() throws IOException, Exception {
		final long communityId = 1L;
		Mockito.when(communityServiceMock.acceptJoinRequest(Mockito.eq(communityId), Mockito.any()))
				.thenReturn(new UpdateMessage(null, null));

		mockMvc.perform(post(PATH + communityId + "/members").contentType(MediaType.APPLICATION_JSON)
				.content(json(new CommunityChangeDto()))).andExpect(status().isCreated());

		Mockito.verify(communityServiceMock, Mockito.times(1)).acceptJoinRequest(Mockito.eq(communityId),
				Mockito.any());
	}

	@Test
	public void acceptJoinRequest_IllegalArgument() throws IOException, Exception {
		final long communityId = 1L;
		Mockito.when(communityServiceMock.acceptJoinRequest(Mockito.eq(communityId), Mockito.any()))
				.thenThrow(new IllegalArgumentException());

		mockMvc.perform(post(PATH + communityId + "/members").contentType(MediaType.APPLICATION_JSON)
				.content(json(new CommunityChangeDto()))).andExpect(status().isPreconditionFailed());

		Mockito.verify(communityServiceMock, Mockito.times(1)).acceptJoinRequest(Mockito.eq(communityId),
				Mockito.any());
	}

	@Test
	public void acceptJoinRequest_NoSuchElement() throws IOException, Exception {
		final long communityId = 1L;
		Mockito.when(communityServiceMock.acceptJoinRequest(Mockito.eq(communityId), Mockito.any()))
				.thenThrow(new NoSuchElementException());

		mockMvc.perform(post(PATH + communityId + "/members").contentType(MediaType.APPLICATION_JSON)
				.content(json(new CommunityChangeDto()))).andExpect(status().isNotFound());

		Mockito.verify(communityServiceMock, Mockito.times(1)).acceptJoinRequest(Mockito.eq(communityId),
				Mockito.any());
	}

	@Test
	public void acceptJoinRequest_IllegalState() throws IOException, Exception {
		final long communityId = 1L;
		Mockito.when(communityServiceMock.acceptJoinRequest(Mockito.eq(communityId), Mockito.any()))
				.thenThrow(new IllegalStateException());

		mockMvc.perform(post(PATH + communityId + "/members").contentType(MediaType.APPLICATION_JSON)
				.content(json(new CommunityChangeDto()))).andExpect(status().isBadRequest());

		Mockito.verify(communityServiceMock, Mockito.times(1)).acceptJoinRequest(Mockito.eq(communityId),
				Mockito.any());
	}

	@Test
	public void acceptJoinRequest_IllegalAccess() throws IOException, Exception {
		final long communityId = 1L;
		Mockito.when(communityServiceMock.acceptJoinRequest(Mockito.eq(communityId), Mockito.any()))
				.thenThrow(new IllegalAccessException());

		mockMvc.perform(post(PATH + communityId + "/members").contentType(MediaType.APPLICATION_JSON)
				.content(json(new CommunityChangeDto()))).andExpect(status().isUnauthorized());

		Mockito.verify(communityServiceMock, Mockito.times(1)).acceptJoinRequest(Mockito.eq(communityId),
				Mockito.any());
	}

	@Test
	public void deleteMember_Ok() throws IOException, Exception {
		final long communityId = 1L;
		final long memberId = 1L;
		Mockito.when(communityServiceMock.deleteMember(communityId, memberId))
				.thenReturn(new UpdateMessage(null, null));

		mockMvc.perform(delete(PATH + communityId + "/members/" + memberId)).andExpect(status().isOk());

		Mockito.verify(communityServiceMock, Mockito.times(1)).deleteMember(communityId, memberId);
	}

	@Test
	public void deleteMember_NoSuchElement() throws IOException, Exception {
		final long communityId = 1L;
		final long memberId = 1L;
		Mockito.when(communityServiceMock.deleteMember(communityId, memberId)).thenThrow(new NoSuchElementException());

		mockMvc.perform(delete(PATH + communityId + "/members/" + memberId)).andExpect(status().isNotFound());

		Mockito.verify(communityServiceMock, Mockito.times(1)).deleteMember(communityId, memberId);
	}

	@Test
	public void deleteMember_IllegalState() throws IOException, Exception {
		final long communityId = 1L;
		final long memberId = 1L;
		Mockito.when(communityServiceMock.deleteMember(communityId, memberId)).thenThrow(new IllegalStateException());

		mockMvc.perform(delete(PATH + communityId + "/members/" + memberId)).andExpect(status().isBadRequest());

		Mockito.verify(communityServiceMock, Mockito.times(1)).deleteMember(communityId, memberId);
	}

	@Test
	public void deleteMember_IllegalAccess() throws IOException, Exception {
		final long communityId = 1L;
		final long memberId = 1L;
		Mockito.when(communityServiceMock.deleteMember(communityId, memberId)).thenThrow(new IllegalAccessException());

		mockMvc.perform(delete(PATH + communityId + "/members/" + memberId)).andExpect(status().isUnauthorized());

		Mockito.verify(communityServiceMock, Mockito.times(1)).deleteMember(communityId, memberId);
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}
