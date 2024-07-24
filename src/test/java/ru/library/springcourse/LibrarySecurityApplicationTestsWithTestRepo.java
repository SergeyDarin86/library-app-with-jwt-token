package ru.library.springcourse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.validation.BindingResult;
import ru.library.springcourse.controllers.BookController;
import ru.library.springcourse.dto.BookDTO;
import ru.library.springcourse.dto.PersonDTO;
import ru.library.springcourse.services.BooksService;
import ru.library.springcourse.util.BookValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// говорит, что нам нужно выполнить sql-скрипт перед выполнением теста
// может быть указана на уровне класса или на уровне любого метода

@Sql(value = {"/create-person-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/delete-person-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties") // указываем файл с пропертями для тестового окружения
public class LibrarySecurityApplicationTestsWithTestRepo {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookController bookController;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    BooksService booksService;

    @Mock
    BookValidator bookValidator;

    @Mock
    BindingResult bindingResult;

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
    void sortedBooksByYear() throws Exception {
        this.mockMvc
                .perform(get("/library/sortedByYear").accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("bookDTOList", Matchers.hasSize(3)));
    }

    @Test
    void searchTest() throws Exception {
        String title = "Основы";

        mockMvc.perform(get("/library/books/search")
                        .param("searchBook", title)
                        .header("Authorization", this.token))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void searchWithExceptionTitleNotEntered() throws Exception {

        mockMvc.perform(get("/library/books/search")
                        .param("searchBook", "")
                        .header("Authorization", this.token))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void searchWithExceptionBookNotFound() throws Exception {

        mockMvc.perform(get("/library/books/search")
                        .param("searchBook", "Тестовый заголовок")
                        .header("Authorization", this.token))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void showBookById() throws Exception {
        int id = 2;
        this.mockMvc.perform(get("/library/books/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token))
                .andExpect(status().isOk());
    }

    @Test
    void showBookWithNotFoundException() throws Exception {
        int id = 12;
        this.mockMvc.perform(get("/library/books/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook() throws Exception {
        int bookId = 2;

        this.mockMvc.perform(delete("/library/books/" + bookId)
                        .header("Authorization", this.token))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteBookWithNotFoundException() throws Exception {
        int bookId = 5;

        this.mockMvc.perform(delete("/library/books/" + bookId)
                        .header("Authorization", this.token))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void newBook() throws Exception {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Машина времени");
        bookDTO.setAuthor("Иван Иванов");
        bookDTO.setYearOfRealise(1890);

        mockMvc.perform(post("/library/newBook")
                        .header("Authorization", this.token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookDTO))
                )
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    void newBookWithLibraryException() throws Exception {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Машина времени");
        bookDTO.setAuthor("Иван Иванов");
        bookDTO.setYearOfRealise(1600);

        doNothing().when(bookValidator).validate(bookDTO, bindingResult);

        mockMvc.perform(post("/library/newBook")
                        .header("Authorization", this.token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookDTO))
                )
                .andExpect(status().is4xxClientError())
                .andDo(print());

    }

    @Test
    void updateBook() throws Exception {

        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Машина времени");
        bookDTO.setAuthor("Тестовый Автор");
        bookDTO.setYearOfRealise(1800);
        int bookId = 3;

        mockMvc.perform(patch("/library/books/" + bookId)
                        .header("Authorization", this.token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookDTO))
                )
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    void updateBookWithNotFoundException() throws Exception {

        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Машина времени");
        bookDTO.setAuthor("Тестовый Автор");
        bookDTO.setYearOfRealise(1800);
        int bookId = 5;

        mockMvc.perform(patch("/library/books/" + bookId)
                        .header("Authorization", this.token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookDTO))
                )
                .andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    void makeBookFree() throws Exception {
        int bookId = 2;

        mockMvc.perform(patch("/library/books/" + bookId + "/makeFree")
                        .header("Authorization", this.token))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    void makeBookFreeWithNotFoundException() throws Exception {
        int bookId = 5;

        mockMvc.perform(patch("/library/books/" + bookId + "/makeFree")
                        .header("Authorization", this.token))
                .andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    void assignPerson() throws Exception {
        int bookId = 4;
        int personId = 2;

        mockMvc.perform(patch("/library/books/" + bookId + "/" + personId + "/assignPerson")
                        .header("Authorization", this.token))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    void assignPersonWithNotAcceptableException() throws Exception {
        int bookId = 3;
        int personId = 2;

        mockMvc.perform(patch("/library/books/" + bookId + "/" + personId + "/assignPerson")
                        .header("Authorization", this.token))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andDo(print());
    }

    @Test
    void showPersonById() throws Exception {
        int personId = 1;
        this.mockMvc.perform(get("/library/people/{id}", personId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token))
                .andExpect(status().isOk());
    }

    @Test
    void showPersonByIdWithNotFoundException() throws Exception {
        int personId = 3;
        this.mockMvc.perform(get("/library/people/{id}", personId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePerson() throws Exception {
        int personId = 2;

        this.mockMvc.perform(delete("/library/people/" + personId)
                        .header("Authorization", this.token))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deletePersonWithNotFoundException() throws Exception {
        int personId = 3;

        this.mockMvc.perform(delete("/library/people/" + personId)
                        .header("Authorization", this.token))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePerson() throws Exception {

        PersonDTO personDTO = new PersonDTO();
        personDTO.setFullName("Дарин Сергей Владимирович");
        personDTO.setYearOfBirthday(1986);
        personDTO.setLogin("user2");
        personDTO.setPassword("user2");
        int personId = 2;

        mockMvc.perform(patch("/library/people/" + personId)
                        .header("Authorization", this.token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(personDTO))
                )
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    void updatePersonWithNotFoundException() throws Exception {

        PersonDTO personDTO = new PersonDTO();
        personDTO.setFullName("Дарин Сергей Владимирович");
        personDTO.setYearOfBirthday(1986);
        personDTO.setLogin("user2");
        personDTO.setPassword("user2");
        int personId = 4;

        mockMvc.perform(patch("/library/people/" + personId)
                        .header("Authorization", this.token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(personDTO))
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

}
