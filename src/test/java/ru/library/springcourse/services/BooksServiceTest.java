package ru.library.springcourse.services;

import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import ru.library.springcourse.dto.BookDTO;
import ru.library.springcourse.models.Book;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.BooksRepository;
import ru.library.springcourse.util.BookResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BooksServiceTest extends TestCase {

    Book book = new Book();

    Book book2 = new Book();

    BookDTO bookDTO = new BookDTO();

    BookDTO bookDTO2 = new BookDTO();
    Person person = new Person();

    BooksService booksService = Mockito.mock(BooksService.class);

    BooksRepository booksRepository = Mockito.mock(BooksRepository.class);

    ModelMapper modelMapper = Mockito.mock(ModelMapper.class);

    List<Book> bookList = new ArrayList<>();

    List<Book> bookListForStartingWith = new ArrayList<>();

    Date date = new Date();

    @Override
    @BeforeEach
    protected void setUp() {
        book.setBookId(1);
        book.setAuthor("Тестовый Автор");
        book.setTitle("Тестовый заголовок");
        book.setYearOfRealise(1999);
        book.setTakenAt(date);

        person.setFullName("Иванов Иван Иванович");
        person.setPersonId(1);
        book.setPerson(person);

        book2.setBookId(1);
        book2.setAuthor("Новый Автор");
        book2.setTitle("Тест на мышление");
        book2.setYearOfRealise(2001);
    }

    @BeforeEach
    void fillingList(){
        bookList.add(book);
    }

    @BeforeEach
    void fillingListBookDTOForStartingWith(){
        bookListForStartingWith.add(book);
        bookListForStartingWith.add(book2);
    }

    @BeforeEach
    void fillingBookDTO(){
        bookDTO.setYearOfRealise(1999);
        bookDTO.setAuthor("Тестовый Автор");
        bookDTO.setTitle("Тестовый заголовок");
    }

    @BeforeEach
    void fillingBookDTO2(){
        bookDTO2.setAuthor("Новый Автор");
        bookDTO2.setTitle("Тест на мышление");
        bookDTO2.setYearOfRealise(2001);
    }

    @Test
    void showByTitle() {
        Mockito.when(booksService.show("Тестовый")).thenReturn(Optional.of(book));
        assertEquals(Optional.of(book), booksService.show("Тестовый"));
        verify(booksService, times(1)).show("Тестовый");
    }

    @Test
    void showById() {
//        Mockito.when(booksService.show(1)).thenReturn(book);
//        assertEquals(book, booksService.show(1));
//        assertEquals(1999, booksService.show(1).getYearOfRealise().intValue());
//
//        assertThat(booksService.show(1).getYearOfRealise()).isEqualTo(1999);

        Mockito.when(booksService.show(1)).thenReturn(book);
        Book expectedBook = booksService.show(1);
        given(booksRepository.findById(1)).willReturn(Optional.of(book));
        Book actualBook = booksRepository.findById(1).get();
        assertThat(actualBook).isEqualTo(expectedBook);
    }

    @Test
    void save() {
        booksRepository.save(book);
        verify(booksRepository, times(1)).save(book);
    }

    @Test
    void delete() {
        booksRepository.deleteById(1);
        verify(booksRepository, times(1)).deleteById(1);
    }


    @Test
    void makeBookFree() {
        Mockito.when(booksService.show(1)).thenReturn(book);
        booksService.show(1).setPerson(null);
        booksService.show(1).setTakenAt(null);

        doNothing().when(booksService).makeBookFree(1);

        assertNull(booksService.show(1).getPerson()); // данный метод предложила Idea вместо (assertEquals)
        assertEquals(null,booksService.show(1).getTakenAt());
    }

    @Test
    void assignPerson() {
        Mockito.when(booksService.show(1)).thenReturn(book);
        booksService.show(1).setPerson(person);
        booksService.show(1).setTakenAt(new Date());

        doNothing().when(booksService).assignPerson(1,1);

        assertEquals(person,booksService.show(1).getPerson());
        assertEquals(book.getTakenAt(),booksService.show(1).getTakenAt());
    }

    @Test
    void findAll() {
        Book bookActual = new Book();
        bookActual.setBookId(1);
        bookActual.setAuthor("Тестовый Автор");
        bookActual.setTitle("Тестовый заголовок");
        bookActual.setYearOfRealise(1999);
        bookActual.setTakenAt(date);

        person.setFullName("Иванов Иван Иванович");
        person.setPersonId(1);
        bookActual.setPerson(person);

        assertThat(bookList.get(0)).isEqualToComparingFieldByField(bookActual);
        assertThat(bookList).contains(bookActual);

        List<Book>bookListActual = new ArrayList<>();
        bookListActual.add(bookActual);
        assertEquals(bookListActual,bookList);

        Mockito.when(booksService.findAll(0,1,true)).thenReturn(bookList);
        assertEquals(bookListActual,booksService.findAll(0,1,true));

        Mockito.when(booksRepository.findAll()).thenReturn(bookList);
        assertEquals(booksRepository.findAll(),booksService.findAll(0,1,true));
    }

    @Test
    void convertToDTOFromBook() {
        when(booksService.convertToDTOFromBook(book)).thenReturn(bookDTO);
        assertEquals(bookDTO,booksService.convertToDTOFromBook(book));

        when(modelMapper.map(book,BookDTO.class)).thenReturn(bookDTO);
        assertEquals(bookDTO,modelMapper.map(book,BookDTO.class));

        assertEquals(booksService.convertToDTOFromBook(book),modelMapper.map(book,BookDTO.class));
    }

    @Test
    void convertToBookFromDTO() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setYearOfRealise(1999);
        bookDTO.setAuthor("Тестовый Автор");
        bookDTO.setTitle("Тестовый заголовок");

        when(booksService.convertToBookFromDTO(bookDTO)).thenReturn(book);
        assertEquals(book,booksService.convertToBookFromDTO(bookDTO));

        when(modelMapper.map(bookDTO, Book.class)).thenReturn(book);
        assertEquals(book,modelMapper.map(bookDTO,Book.class));

        assertEquals(modelMapper.map(bookDTO, Book.class),booksService.convertToBookFromDTO(bookDTO));
    }

    @Test
    void getBookOwner() {
        Mockito.when(booksService.getBookOwner(1)).thenReturn(Optional.of(person));
        Person actualPerson = booksService.getBookOwner(1).get();

        Mockito.when(booksRepository.findById(1)).thenReturn(Optional.of(book));
        Person expectedPerson = book.getPerson();

        assertThat(actualPerson).isEqualTo(expectedPerson);
    }

    @Test
    void getBookListByTitleStartingWith() {
        when(modelMapper.map(book,BookDTO.class)).thenReturn(bookDTO);
        when(modelMapper.map(book2,BookDTO.class)).thenReturn(bookDTO2);
        List<BookDTO>bookDTOList = new ArrayList<>();
        bookDTOList.add(bookDTO);
        bookDTOList.add(bookDTO2);

        BookResponse bookResponse = new BookResponse(bookDTOList);

        Mockito.when(booksService.getBookListByTitleStartingWith("Тест")).thenReturn(bookResponse);
        assertEquals(bookResponse, booksService.getBookListByTitleStartingWith("Тест"));
    }

    @Test
    void update() {
        Book updatedBook = Mockito.mock(Book.class);

        doNothing().when(booksService).update(1,updatedBook);
        booksService.update(1,updatedBook);
        verify(booksService,times(1)).update(1,updatedBook);

        booksRepository.save(updatedBook);
        when(booksRepository.save(updatedBook)).thenReturn(updatedBook);
        verify(booksRepository,times(1)).save(updatedBook);

        Date dateToSet = new Date();
        updatedBook.setTakenAt(dateToSet);
        verify(updatedBook,times(1)).setTakenAt(dateToSet);
        doNothing().when(updatedBook).setTakenAt(dateToSet);

        updatedBook.setBookId(1);
        verify(updatedBook,times(1)).setBookId(1);
        doNothing().when(updatedBook).setBookId(1);

        updatedBook.setPerson(null);
        verify(updatedBook,times(1)).setPerson(null);
        doNothing().when(updatedBook).setPerson(null);

        when(updatedBook.getTakenAt()).thenReturn(dateToSet);
        assertEquals(dateToSet, updatedBook.getTakenAt());

    }
}