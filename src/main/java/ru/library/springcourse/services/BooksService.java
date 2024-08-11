package ru.library.springcourse.services;

//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.library.springcourse.dto.BookDTO;
import ru.library.springcourse.models.Book;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.BooksRepository;
import ru.library.springcourse.securuty.PersonDetails;
import ru.library.springcourse.util.BookResponse;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с книгами в библиотеке (добавление, редактирование, удаление, поиск, отображение списка всех книг, сортировка)
 *
 * @author Sergey D.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    private final ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public BooksService(BooksRepository booksRepository, ModelMapper modelMapper) {
        this.booksRepository = booksRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Метод для поиска всех книг в библиотеке с возможностью пагинации
     *
     * @param isSortedByYear сортировка по году издания книги
     * @param limitOfBooks количество книг на странице
     * @param page номер отображаемой страницы
     *
     * @return Список книг
     */
    public List<Book> findAll(Boolean isSortedByYear, Integer page, Integer limitOfBooks) {
        log.info("Start method findAll() for bookService");

        if (page != null && limitOfBooks != null && isSortedByYear != null) {
            return booksRepository.findAll(PageRequest.of(page, limitOfBooks, Sort.by("yearOfRealise"))).getContent();
        } else if (isSortedByYear != null && isSortedByYear) {
            return booksRepository.findAll(Sort.by("yearOfRealise"));
        } else if (page != null && limitOfBooks != null) {
            return booksRepository.findAll(PageRequest.of(page, limitOfBooks)).getContent();
        } else {
            return booksRepository.findAll();
        }

    }

    /**
     * Метод для получения списка книг, отсортированного по году издания
     *
     * @return Отсортированный список книг
     */
    public BookResponse sortedBooksByYear() {
        log.info("Start method sortedBooksByYear() for bookService");
        return new BookResponse(booksRepository.findAll(Sort.by("yearOfRealise"))
                .stream().map(this::convertToDTOFromBook).toList());
    }

    /**
     * Метод для получения экземпляра книги по её идентификационному номеру
     *
     * @param id Идентификационный номер книги
     *
     * @return Экземпляр Книги
     */

    public Book show(int id) {
        log.info("Start method show(id) for bookService, bookId is: {} ", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        System.out.println(personDetails.getPerson());

        return booksRepository.findById(id).orElse(null);
    }

    /**
     * Метод для получения экземпляра книги по её заголовку (полное совпадение)
     *
     * @param title  Заголовок книги
     *
     * @return Экземпляр книги либо NULL
     */

    public Optional<Book> show(String title) {
        log.info("Start method show(title) for bookService, bookTitle is: {} ", title);
        return booksRepository.findBookByTitle(title);
    }

    /**
     * Метод для сохранения экземпляра книги
     *
     * @param book  Экземпляр книги
     *
     */
    @Transactional
    public void save(Book book) {
        log.info("Start method save(Book) for bookService, book is: {} ", book);
        booksRepository.save(book);
    }

    /**
     * Метод для обновления экземпляра книги
     *
     * @param id Идентификационный номер книги
     * @param updatedBook Редактируемая книга
     *
     */
    @Transactional
    public void update(int id, Book updatedBook) {
        log.info("Start method update(id, Book) for bookService, id is: {} ", id);
        if (!getBookOwner(id).isPresent())
            updatedBook.setPerson(null);
        if (getBookOwner(id).isPresent())
            updatedBook.setPerson(getBookOwner(id).get());

        updatedBook.setTakenAt(booksRepository.findById(id).get().getTakenAt());
        updatedBook.setBookId(id);
        booksRepository.save(updatedBook);
    }

    /**
     * Метод для удаления экземпляра книги
     *
     * @param id Идентификационный номер книги
     *
     */

    @Transactional
    public void delete(int id) {
        log.info("Start method delete(id) for bookService, id is: {} ", id);
        booksRepository.deleteById(id);
    }

    /**
     * Метод для освобождения книги (сдача книги читателем в библиотеку)
     *
     * @param id Идентификационный номер книги
     *
     */
    @Transactional
    public void makeBookFree(int id) {
        log.info("Start method makeBookFree(id) for bookService, id is: {}", id);
        show(id).setTakenAt(null);
        show(id).setPerson(null);
    }

    /**
     * Метод для закрепления книги за читателем
     *
     * @param bookId Идентификационный номер книги
     * @param personId Идентификационный номер читателя
     *
     */
    @Transactional
    public void assignPerson(int bookId, int personId) {
        log.info("Start method assignPerson(bookId, personId) for bookService, bookId is: {}, personId is : {} ", bookId, personId);
        Session session = entityManager.unwrap(Session.class);
        Person person = session.getReference(Person.class, personId);
        show(bookId).setTakenAt(new Date());
        show(bookId).setPerson(person);
    }

    /**
     * Метод для получения экземпляра книги по первоначальному совпадению в названии книги
     *
     * @param title  Заголовок книги (первые буквы заголовка)
     *
     * @return Список найденных книг
     */
    public BookResponse getBookListByTitleStartingWith(String title) {
        log.info("Start method getBookListByTitleStartingWith(title) for bookService, title is: {} ", title);
        return new BookResponse(booksRepository.findBookByTitleStartingWith(title)
                .stream().map(this::convertToDTOFromBook).toList());
    }

    /**
     * Метод для получения читателя, у кого находится книга в данный момент
     *
     * @param bookId Идентификационный номер книги
     *
     * @return Экземпляр читателя или NULL
     */
    public Optional<Person> getBookOwner(int bookId) {
        return booksRepository.findById(bookId).map(Book::getPerson);
    }

    /**
     * Метод для преобразования BookDTO в экземпляр книги
     *
     * @param bookDTO Объект DTO для книги
     *
     * @return Экземпляр книги
     */
    public Book convertToBookFromDTO(BookDTO bookDTO) {
        return modelMapper.map(bookDTO, Book.class);
    }

    /**
     * Метод для получения экземпляра книги
     *
     * @param id Идентификационный номер книги
     * @param bookDTO Объект DTO для книги
     *
     * @return Экземпляр книги
     */
    @Transactional
    public Book getConvertedBook(int id, BookDTO bookDTO) {
        log.info("Start method getConvertedBook(bookId, BookDTO) for bookService, BookId is: {}", id);

        Book convertedBook = convertToBookFromDTO(bookDTO);
        convertedBook.setBookId(id);
        return convertedBook;
    }

    /**
     * Метод для преобразования экземпляра Book в объект DTO
     *
     * @param book Объект экземпляр книги
     *
     * @return DTO для книги
     */
    public BookDTO convertToDTOFromBook(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }

    /**
     * Метод для поиска всех книг в библиотеке с возможностью пагинации (используется в контроллере)
     *
     * @param isSortedByYear сортировка по году издания книги
     * @param limitOfBooks количество книг на странице
     * @param page номер отображаемой страницы
     *
     * @return BookResponse
     */
    public BookResponse getAllBooks(Boolean isSortedByYear, Integer page, Integer limitOfBooks) {
        return new BookResponse(findAll(isSortedByYear, page, limitOfBooks)
                .stream().map(this::convertToDTOFromBook).toList());
    }

}
