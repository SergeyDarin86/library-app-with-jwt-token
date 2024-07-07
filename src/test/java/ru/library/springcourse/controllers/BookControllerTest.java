package ru.library.springcourse.controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.library.springcourse.dto.BookDTO;
import ru.library.springcourse.dto.PersonDTO;
import ru.library.springcourse.models.Book;
import ru.library.springcourse.repositories.BooksRepository;
import ru.library.springcourse.services.BooksService;
import ru.library.springcourse.services.PeopleService;
import ru.library.springcourse.util.BookResponse;
import ru.library.springcourse.util.LibraryExceptionNotFound;
import ru.library.springcourse.util.PersonResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class BookControllerTest {

    @Mock
    private PeopleService peopleService;

    @Mock
    private BooksService booksService;
    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;

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
        //TODO: разобраться почему то не видит Body!?


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

        mockMvc.perform(get("/library/books/{id}",1))
                .andExpect(status().is4xxClientError())
                .andDo(print());

        Mockito.verify(booksService, Mockito.times(4)).show(id);
    }

    @Mock
    private BooksRepository booksRepository;

    ModelMapper modelMapper = Mockito.mock(ModelMapper.class);

    @Test
    void showBookById() throws Exception {
        BookDTO bookDTO = Mockito.mock(BookDTO.class);
        Book book = Mockito.mock(Book.class);
        int bookId = 1;
        book.setBookId(bookId);

        when(booksRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(booksService.show(bookId)).thenReturn(book);

        when(booksService.convertToDTOFromBook(book)).thenReturn(bookDTO);
        when(modelMapper.map(book,BookDTO.class)).thenReturn(bookDTO);

        booksService.show(bookId);
//        Mockito.verify(booksService, Mockito.times(1)).show(bookId);
        when(this.bookController.showBook(bookId)).thenReturn(ResponseEntity.ok(bookDTO));
        mockMvc.perform(get("/library/books/" + bookId))
                .andExpect(status().isOk())
                .andDo(print());

    }

}