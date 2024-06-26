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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    public List<Book> findAllBooksByPerson(Person person) {
        countDaysTheBookIsTakenByPersonNew(person);
        return booksRepository.findAllByPerson(person);
    }

    public void countDaysTheBookIsTakenByPersonNew(Person person) {
        booksRepository.findAllByPerson(person).
                forEach(book -> book.setIsTakenMoreThan10Days(differenceBetweenTwoDates(countOfDaysForSingleDate(new Date()), countOfDaysForSingleDate(book.getTakenAt())) > 10));
    }

    //получаем количество дней, прошедших от эпохальной даты
    public Integer countOfDaysForSingleDate(Date date) {
        return (int) date.toInstant().getEpochSecond() / 60 / 60 / 24;
    }

    // высчитываем разницу в днях между текущей датой и датой, когда взяли книгу
    public Integer differenceBetweenTwoDates(int daysForToday, int daysForTakenBook) {
        return daysForToday - daysForTakenBook;
    }

    public List<Book> findAll(Integer page, Integer limitOfBooks, Boolean isSortedByYear) {

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

    public List<Book> sortedBooks() {
        return booksRepository.findAll(Sort.by("yearOfRealise"));
    }

    public Book show(int id) {
        log.info("Start method show(id) for bookService, bookId is: {} ", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        System.out.println(personDetails.getPerson());

        return booksRepository.findById(id).orElse(null);
    }

    public Optional<Book> show(String title) {
        log.info("Start method show(title) for bookService, bookTitle is: {} ", title);
        return booksRepository.findBookByTitle(title);
    }

    @Transactional
    public void save(Book book) {
        log.info("Start method save(Book) for bookService, book is: {} ", book);
        booksRepository.save(book);
    }

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

    @Transactional
    public void delete(int id) {
        log.info("Start method delete(id) for bookService, id is: {} ", id);
        booksRepository.deleteById(id);
    }

    @Transactional
    public void makeBookFree(int id) {
        log.info("Start method makeBookFree(id) for bookService, id is: {}", id);
        show(id).setTakenAt(null);
        show(id).setPerson(null);
    }

    @Transactional
    public void assignPerson(int bookId, int personId) {
        log.info("Start method assignPerson(bookId, personId) for bookService, bookId is: {}, personId is : {} ", bookId, personId);
        Session session = entityManager.unwrap(Session.class);
        Person person = session.getReference(Person.class, personId);
        show(bookId).setTakenAt(new Date());
        show(bookId).setPerson(person);
    }

    public List<Book> getBookListByTitleStartingWith(String title) {
        log.info("Start method getBookListByTitleStartingWith(title) for bookService, title is: {} ", title);
        return booksRepository.findBookByTitleStartingWith(title);
    }

    public Optional<Person> getBookOwner(int bookId) {
        return booksRepository.findById(bookId).map(Book::getPerson);
    }

    public Book convertToBookFromDTO(BookDTO bookDTO){
        return modelMapper.map(bookDTO, Book.class);
    }

    public BookDTO convertToDTOFromBook(Book book){
        return modelMapper.map(book, BookDTO.class);
    }

}
