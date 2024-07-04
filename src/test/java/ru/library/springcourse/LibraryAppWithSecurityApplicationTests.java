package ru.library.springcourse;

import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ru.library.springcourse.controllers.BookController;
import ru.library.springcourse.dto.AuthenticationDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LibraryAppWithSecurityApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private BookController bookController;

	String token;

	@BeforeEach
	void setUp() throws Exception{
		ResultActions resultActions = this.mockMvc
				.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content("{\"login\":\"user\",\"password\":\"user\"}")
						.accept(MediaType.APPLICATION_JSON_VALUE));

		MvcResult mvcResult = resultActions.andDo(print()).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();

		JSONObject jsonObject = new JSONObject(contentAsString);
		this.token = "Bearer " + jsonObject.getString("jwt-token");

	}

	@Test
	void contextLoads() {
		assertThat(bookController).isNotNull();
	}

	@Test
	void getAllPeople() throws Exception{
		this.mockMvc
				.perform(get("/library/people").accept(MediaType.APPLICATION_JSON)
						.header("Authorization",this.token))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("personDTOList", Matchers.hasSize(13)));
	}


	@Test
	void adminPage() throws Exception{
		this.mockMvc.perform(get("/library/admin")
						.accept(MediaType.APPLICATION_JSON).header("Authorization",this.token))
				.andExpect(content().string(containsString("/people/adminPage")))
				.andExpect(status().is2xxSuccessful())
				.andDo(print());
	}

}
