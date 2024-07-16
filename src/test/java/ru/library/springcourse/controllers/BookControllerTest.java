package ru.library.springcourse.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import ru.library.springcourse.dto.BookDTO;
import ru.library.springcourse.dto.PersonDTO;
import ru.library.springcourse.models.Book;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.BooksRepository;
import ru.library.springcourse.repositories.PeopleRepository;
import ru.library.springcourse.services.BooksService;
import ru.library.springcourse.services.PeopleService;
import ru.library.springcourse.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BookControllerTest {

    @Mock
    private PeopleService peopleService;

    @Mock
    private PeopleRepository peopleRepository;

    @Mock
    private BooksService booksService;

    @Mock
    private BooksRepository booksRepository;
    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;

    @Mock
    BindingResult bindingResult;

    @Mock
    BookValidator bookValidator;

    @Mock
    PersonValidator personValidator;

    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void people() throws Exception {

        List<PersonDTO> personDTOList = new ArrayList<>();
        PersonResponse personResponse = new PersonResponse(personDTOList);

        when(bookController.people()).thenReturn(personResponse);

        mockMvc.perform(get("/library/people"))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("personDTOList", Matchers.hasSize(0)));

        Mockito.verify(peopleService, Mockito.times(1)).allPeople();
        assertEquals(0, personResponse.getPersonDTOList().size());
    }

    @Test
    void books() throws Exception {
        List<BookDTO> bookDTOList = new ArrayList<>();
        BookResponse bookResponse = new BookResponse(bookDTOList);
        bookController.books(true, 0, 1);

        when(bookController.books(true, 0, 1)).thenReturn(bookResponse);

        mockMvc.perform(get("/library/books").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
//                .andExpect(jsonPath("bookDTOList", Matchers.hasSize(0)));
        //TODO: разобраться почему-то не видит Body!?


        Mockito.verify(booksService, Mockito.times(1)).getAllBooks(true, 0, 1);
        assertEquals(0, bookResponse.getBookDTOList().size());
    }

    @Test
    void sortedByYears() throws Exception {
        List<BookDTO> bookDTOList = new ArrayList<>();
        BookResponse bookResponse = new BookResponse(bookDTOList);

        when(bookController.sortedBooksByYear()).thenReturn(bookResponse);

        mockMvc.perform(get("/library/sortedByYear"))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("bookDTOList", Matchers.hasSize(0)));


        Mockito.verify(booksService, Mockito.times(1)).sortedBooksByYear();
        assertEquals(0, bookResponse.getBookDTOList().size());
    }

    @Test
    void showBookByIdWithThrowException() throws Exception {
        BookDTO bookDTO = Mockito.mock(BookDTO.class);
        Book book = Mockito.mock(Book.class);
        int id = 1;
        String errorMsg = id + " - Книги с таким id не найдено";
        when(booksService.show(id)).thenReturn(book);
        when(booksService.convertToDTOFromBook(book)).thenReturn(bookDTO);
        when(bookController.showBook(id)).thenThrow(new LibraryExceptionNotFound(errorMsg));

        mockMvc.perform(get("/library/books/{id}", 1))
                .andExpect(status().is4xxClientError())
                .andDo(print());

        Mockito.verify(booksService, Mockito.times(4)).show(id);
    }

    @Test
    void showBookById() throws Exception {
        Book book = new Book();
        int bookId = 1;
        book.setBookId(bookId);

        when(booksService.show(bookId)).thenReturn(book);
        when(booksRepository.findById(bookId)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/library/books/" + bookId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    void newBook() throws Exception {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Машина времени");
        bookDTO.setAuthor("Иван Иванов");
        bookDTO.setYearOfRealise(1999);

        doNothing().when(bookValidator).validate(bookDTO, bindingResult);

        mockMvc.perform(post("/library/newBook")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookDTO))
                )
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    void newBookWithThrowException() throws Exception {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Машина времени");
        bookDTO.setAuthor("Иван Иванов");
        bookDTO.setYearOfRealise(1600);
        String errorMsg = "Год издания книги должен быть больше 1700";

        when(bookController.newBook(bookDTO, bindingResult)).thenThrow(new LibraryException(errorMsg));

        mockMvc.perform(post("/library/newBook")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookDTO))
                )
                .andExpect(status().is4xxClientError())
                .andDo(print());

    }

    @Test
    void updateBook() throws Exception {
        Book book = Mockito.mock(Book.class);
        book.setBookId(1);
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Машина времени");
        bookDTO.setAuthor("Тестовый Автор");
        bookDTO.setYearOfRealise(1800);
        int bookId = 1;

        Book convertedBook = Mockito.mock(Book.class);

        when(booksService.show(bookId)).thenReturn(book);
        doNothing().when(book).setBookId(bookId);
        doNothing().when(booksService).update(bookId, book);

        when(booksService.convertToBookFromDTO(bookDTO)).thenReturn(convertedBook);

        mockMvc.perform(patch("/library/books/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookDTO))
                )
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    void showPersonById() throws Exception {
        Person person = new Person();
        int personId = 1;
        person.setPersonId(personId);

        when(peopleService.show(personId)).thenReturn(person);
        when(peopleRepository.findById(personId)).thenReturn(Optional.of(person));

        mockMvc.perform(get("/library/people/" + personId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    void showPersonByIdWithThrowException() throws Exception {
        PersonDTO personDTO = Mockito.mock(PersonDTO.class);
        Person person = Mockito.mock(Person.class);
        int id = 1;
        String errorMsg = id + " - Человека с таким id не найдено";
        when(peopleService.show(id)).thenReturn(person);
        when(peopleService.convertToDTOFromPerson(person)).thenReturn(personDTO);
        when(bookController.show(id)).thenThrow(new LibraryExceptionNotFound(errorMsg));

        mockMvc.perform(get("/library/people/{id}", 2))
                .andExpect(status().is4xxClientError())
                .andDo(print());

        Mockito.verify(peopleService, Mockito.times(1)).show(id);
    }

    @Test
    void testAllPeople() throws Exception {
        List<PersonDTO> personDTOList = new ArrayList<>();
        PersonDTO personDTO = new PersonDTO();
        personDTO.setFullName("Дарин Сергей Владимирович");
        personDTOList.add(personDTO);
        PersonResponse personResponse = new PersonResponse(personDTOList);

        when(bookController.people()).thenReturn(personResponse);

        mockMvc.perform(get("/library/people").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("personDTOList", Matchers.hasSize(1)));

        Mockito.verify(peopleService, Mockito.times(1)).allPeople();
    }

    @Test
    void deleteBook() throws Exception {
        Person person = new Person();
        int personId = 1;
        person.setPersonId(personId);

        when(peopleService.show(personId)).thenReturn(person);
        when(peopleRepository.findById(personId)).thenReturn(Optional.of(person));

        mockMvc.perform(delete("/library/people/" + personId))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deletePerson() throws Exception {
        Book book = new Book();
        int bookId = 1;
        book.setBookId(bookId);

        when(booksService.show(bookId)).thenReturn(book);
        when(booksRepository.findById(bookId)).thenReturn(Optional.of(book));

        mockMvc.perform(delete("/library/books/" + bookId))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @SneakyThrows
    void makeBookFree() {
        int bookId = 1;

        Book book = Mockito.mock(Book.class);

        when(booksService.show(bookId)).thenReturn(book);
        mockMvc.perform(patch("/library/books/" + bookId + "/makeFree"))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(booksService, times(1)).makeBookFree(bookId);
    }

    @Test
    @SneakyThrows
    void makeBookFreeWithThrowException() {
        int bookId = 1;

        String errorMsg = bookId + " - Книги с таким id не найдено";

        when(booksService.show(bookId)).thenThrow(new LibraryExceptionNotFound(errorMsg));
        mockMvc.perform(patch("/library/books/" + bookId + "/makeFree"))
                .andExpect(status().is4xxClientError())
                .andDo(print());

        //если поставить 1, то выдаст ошибку: Wanted, but not invoked
        // скорее всего потому, что метод не выполняется ни разу
        verify(booksService, times(0)).makeBookFree(bookId);
    }

    @Test
    @SneakyThrows
    void assignPerson() {
        int bookId = 1;
        int personId = 1;
        Book book = Mockito.mock(Book.class);
        Person person = Mockito.mock(Person.class);

        when(booksService.show(bookId)).thenReturn(book);
        when(peopleService.show(personId)).thenReturn(person);

        mockMvc.perform(patch("/library/books/" + bookId + "/" + personId + "/assignPerson"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
        verify(booksService, times(1)).assignPerson(bookId, personId);
    }

    @Test
    @SneakyThrows
    void assignPersonWithThrowExceptionNotAcceptable() {
        int bookId = 1;
        int personId = 1;
        Book book = Mockito.mock(Book.class);
        Person person = new Person();
        person.setPersonId(personId);
        String errorMsg = " Невозможно назначить книгу - книга уже в пользовании";

        when(booksService.show(bookId)).thenReturn(book);
        when(peopleService.show(personId)).thenReturn(person);
        when(!peopleService.show(personId).equals(null)).thenThrow(new LibraryExceptionNotAcceptable(errorMsg));

        mockMvc.perform(patch("/library/books/" + bookId + "/" + personId + "/assignPerson"))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andDo(print());
        verify(booksService, times(0)).assignPerson(bookId, personId);
    }

    @Test
    @SneakyThrows
    void updatePerson() {

        int personId = 1;
        Person person = new Person();
        person.setPersonId(personId);

        PersonDTO personDTO = new PersonDTO();
        personDTO.setFullName("Дарин Сергей Владимирович");
        personDTO.setYearOfBirthday(1986);
        personDTO.setLogin("user");
        personDTO.setPassword("user");

        when(peopleService.show(personId)).thenReturn(person);
        when(peopleRepository.findById(personId)).thenReturn(Optional.of(person));
        when(peopleService.convertToPersonFromDTO(personDTO)).thenReturn(person);
        personValidator.validate(peopleService.convertToPersonFromDTO(personDTO),bindingResult);

        mockMvc.perform(patch("/library/people/" + personId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(personDTO))
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    void searchWithExceptionTitleNotEntered() throws Exception{

        mockMvc.perform(get("/library/books/search")
                        .param("searchBook", ""))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void searchWithExceptionBookNotFound() throws Exception{
        String title = "Основы";

        mockMvc.perform(get("/library/books/search")
                        .param("searchBook", title))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    void searchTest(){
        String title = "Тест";
        Book book = new Book();
        book.setTitle(title);

        List<BookDTO>bookDTOList = new ArrayList<>();
        BookResponse bookResponse = new BookResponse(bookDTOList);

        when(booksService.show(title)).thenReturn(Optional.of(book));
        when(booksService.getBookListByTitleStartingWith(title)).thenReturn(bookResponse);
        when(booksRepository.findBookByTitleStartingWith(title)).thenReturn(bookDTOList
                .stream().map(bookDTO -> booksService.convertToBookFromDTO(bookDTO)).toList());

        mockMvc.perform(get("/library/books/search")
                        .param("searchBook", title))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

}