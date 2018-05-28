package ch.fhnw.wodss.backend.web;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import ch.fhnw.wodss.backend.businesslogic.MatchService;
import ch.fhnw.wodss.backend.dto.MatchChangeDto;
import ch.fhnw.wodss.backend.dto.MatchDto;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { BackendApplication.class })
@WebAppConfiguration
public class MatchControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private MatchService matchServiceMock;

	private MockMvc mockMvc;

	private HttpMessageConverter<Object> mappingJackson2HttpMessageConverter;

	private final static String PATH = "/api/matches/";

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
		Mockito.reset(matchServiceMock);
	}

	@Test
	public void findAll_Ok() throws IOException, Exception {
		Mockito.when(matchServiceMock.findAll()).thenReturn(new LinkedList<MatchDto>());

		mockMvc.perform(get(PATH)).andExpect(status().isOk());

		Mockito.verify(matchServiceMock, Mockito.times(1)).findAll();
	}

	@Test
	public void update_Ok() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(matchServiceMock.update(Mockito.eq(id), Mockito.any())).thenReturn(new UpdateMessage(null, null));

		mockMvc.perform(put(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new MatchChangeDto())))
				.andExpect(status().isOk());

		Mockito.verify(matchServiceMock, Mockito.times(1)).update(Mockito.eq(id), Mockito.any());
	}

	@Test
	public void update_NoSuchElement() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(matchServiceMock.update(Mockito.eq(id), Mockito.any())).thenThrow(new NoSuchElementException());

		mockMvc.perform(put(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new MatchChangeDto())))
				.andExpect(status().isNotFound());

		Mockito.verify(matchServiceMock, Mockito.times(1)).update(Mockito.eq(id), Mockito.any());
	}

	@Test
	public void update_IllegalArgument() throws IOException, Exception {
		final long id = 1L;
		Mockito.when(matchServiceMock.update(Mockito.eq(id), Mockito.any())).thenThrow(new IllegalArgumentException());

		mockMvc.perform(put(PATH + id).contentType(MediaType.APPLICATION_JSON).content(json(new MatchChangeDto())))
				.andExpect(status().isPreconditionFailed());

		Mockito.verify(matchServiceMock, Mockito.times(1)).update(Mockito.eq(id), Mockito.any());
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}
