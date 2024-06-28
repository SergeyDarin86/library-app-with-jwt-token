package ru.library.springcourse.services;

import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.library.springcourse.models.Book;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BooksServiceTest extends TestCase {

    Book book = new Book();

    @Mock
    BooksService booksService = Mockito.mock(BooksService.class);

    @Override
    @BeforeEach
    protected void setUp(){
        book.setBookId(1);
        book.setAuthor("Тестовый Автор");
        book.setTitle("Тестовый заголовок");
        book.setYearOfRealise(1999);
    }


    // тестирование поиска по заголовку книги
    @Test
    void testShow() {
        Mockito.when(booksService.show("Тестовый")).thenReturn(Optional.of(book));
        assertEquals(Optional.of(book),booksService.show("Тестовый"));
    }

    // тестовый метод - не относится к проекту
    @Test
    void showTitle() {
        Mockito.when(booksService.findBookByTitle("Тестовый")).thenReturn(book);
        assertEquals(book,booksService.findBookByTitle("Тестовый"));
    }

    @Test
    void show() {
        Mockito.when(booksService.show(1)).thenReturn(book);
        assertEquals(book,booksService.show(1));
    }
}