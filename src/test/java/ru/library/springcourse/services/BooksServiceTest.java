package ru.library.springcourse.services;

import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.library.springcourse.models.Book;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.BooksRepository;

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
    Person person = new Person();

    BooksService booksService = Mockito.mock(BooksService.class);

    BooksRepository booksRepository = Mockito.mock(BooksRepository.class);

    List<Book> bookList = new ArrayList<>();

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
    }

    @BeforeEach
    void fillingList(){
        bookList.add(book);
    }

    // тестирование поиска по заголовку книги
    @Test
    void testShow() {
        Mockito.when(booksService.show("Тестовый")).thenReturn(Optional.of(book));
        assertEquals(Optional.of(book), booksService.show("Тестовый"));
    }

    @Test
    void show() {
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
    }
}