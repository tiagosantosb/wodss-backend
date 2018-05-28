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
import ch.fhnw.wodss.backend.businesslogic.BetService;
import ch.fhnw.wodss.backend.dto.BetChangeDto;
import ch.fhnw.wodss.backend.dto.BetDto;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { BackendApplication.class })
@WebAppConfiguration
public class BetControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private BetService betServiceMock;

	private MockMvc mockMvc;

	private HttpMessageConverter<Object> mappingJackson2HttpMessageConverter;

	private final static String PATH = "/api/bets/";

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
		Mockito.reset(betServiceMock);
	}

	@Test
	public void findAll_Ok() throws IOException, Exception {
		Mockito.when(betServiceMock.findAll()).thenReturn(new LinkedList<BetDto>());

		mockMvc.perform(get(PATH)).andExpect(status().isOk());

		Mockito.verify(betServiceMock, Mockito.times(1)).findAll();
	}

	@Test
	public void create_Ok() throws IOException, Exception {
		Mockito.when(betServiceMock.create(Mockito.any())).thenReturn(new UpdateMessage(null, null));

		mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(json(new BetDto())))
				.andExpect(status().isCreated());

		Mockito.verify(betServiceMock, Mockito.times(1)).create(Mockito.any());
	}

	@Test
	public void create_IllegalArgument() throws IOException, Exception {
		Mockito.when(betServiceMock.create(Mockito.any())).thenThrow(new IllegalArgumentException());

		mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(json(new BetDto())))
				.andExpect(status().isPreconditionFailed());

		Mockito.verify(betServiceMock, Mockito.times(1)).create(Mockito.any());
	}

	@Test
	public void create_IllegalState() throws IOException, Exception {
		Mockito.when(betServiceMock.create(Mockito.any())).thenThrow(new IllegalStateException());

		mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(json(new BetDto())))
				.andExpect(status().isBadRequest());

		Mockito.verify(betServiceMock, Mockito.times(1)).create(Mockito.any());
	}

	@Test
	public void update_Ok() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(betServiceMock.update(Mockito.eq(id), Mockito.any())).thenReturn(new UpdateMessage(null, null));

		mockMvc.perform(put(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new BetChangeDto())))
				.andExpect(status().isOk());

		Mockito.verify(betServiceMock, Mockito.times(1)).update(Mockito.eq(id), Mockito.any());
	}

	@Test
	public void update_IllegalArgument() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(betServiceMock.update(Mockito.eq(id), Mockito.any())).thenThrow(new IllegalArgumentException());

		mockMvc.perform(put(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new BetChangeDto())))
				.andExpect(status().isPreconditionFailed());

		Mockito.verify(betServiceMock, Mockito.times(1)).update(Mockito.eq(id), Mockito.any());
	}

	@Test
	public void update_NoSuchElement() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(betServiceMock.update(Mockito.eq(id), Mockito.any())).thenThrow(new NoSuchElementException());

		mockMvc.perform(put(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new BetChangeDto())))
				.andExpect(status().isNotFound());

		Mockito.verify(betServiceMock, Mockito.times(1)).update(Mockito.eq(id), Mockito.any());
	}

	@Test
	public void update_IllegalAccess() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(betServiceMock.update(Mockito.eq(id), Mockito.any())).thenThrow(new IllegalAccessException());

		mockMvc.perform(put(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new BetChangeDto())))
				.andExpect(status().isUnauthorized());

		Mockito.verify(betServiceMock, Mockito.times(1)).update(Mockito.eq(id), Mockito.any());
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}
