package ru.library.springcourse;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ru.library.springcourse.controllers.BookController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = {"/create-person-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/delete-person-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class LibraryAppWithSecurityApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookController bookController;

    String token;

    @BeforeEach
    void setUp() throws Exception {
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
    void adminPage() throws Exception {
        this.mockMvc.perform(get("/library/admin")
                        .accept(MediaType.APPLICATION_JSON).header("Authorization", this.token))
                .andExpect(content().string(containsString("/people/adminPage")))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    void getAllPeople() throws Exception {
        this.mockMvc
                .perform(get("/library/people").accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("personDTOList", Matchers.hasSize(2)));
    }

    @Test
    void getAllBooks() throws Exception {
        this.mockMvc
                .perform(get("/library/books").accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("bookDTOList", Matchers.hasSize(3)));
    }

    @Test
    void showBookById() throws Exception {
        int id = 2;
        this.mockMvc.perform(get("/library/books/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", this.token))
                .andExpect(status().isOk());
//        verify(bookController,times(1)).showBook(id);
    }

    @Test
    void showBookWithThrowException() throws Exception {
        int id = 150;
        this.mockMvc.perform(get("/library/books/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token))
                .andExpect(status().isNotFound());
//        verify(bookController,times(1)).showBook(id);
    }

    @Test
    void showPersonById() throws Exception {
        int personId = 1;
        this.mockMvc.perform(get("/library/people/{id}", personId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token))
                .andExpect(status().isOk());
    }

}
