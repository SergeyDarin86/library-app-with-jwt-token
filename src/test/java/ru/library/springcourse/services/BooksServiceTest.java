package ru.library.springcourse.services;

import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.library.springcourse.models.Book;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.BooksRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BooksServiceTest extends TestCase {

    Book book = new Book();
    Person person = new Person();

    @Mock
    BooksService booksService = Mockito.mock(BooksService.class);

    @Mock
    BooksRepository repository = Mockito.mock(BooksRepository.class);

    @Override
    @BeforeEach
    protected void setUp() {
        book.setBookId(1);
        book.setAuthor("Тестовый Автор");
        book.setTitle("Тестовый заголовок");
        book.setYearOfRealise(1999);
        book.setTakenAt(new Date());

        person.setFullName("Иванов Иван Иванович");
        book.setPerson(person);
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
        given(repository.findById(1)).willReturn(Optional.of(book));
        Book actualBook = repository.findById(1).get();
        assertThat(actualBook).isEqualTo(expectedBook);
    }

    @Test
    void save() {
        repository.save(book);
        verify(repository, times(1)).save(book);
    }

    @Test
    void delete() {
        repository.deleteById(1);
        verify(repository, times(1)).deleteById(1);
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
}