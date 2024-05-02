package ru.library.springcourse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.library.springcourse.models.Book;
import ru.library.springcourse.models.Person;

import java.util.List;
import java.util.Optional;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {

    List<Book> findAllByPerson(Person person);

    Optional<Book> findBookByTitle(String title);

    List<Book> findBookByTitleStartingWith(String title);

}
