package ru.library.springcourse.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.library.springcourse.models.Book;
import ru.library.springcourse.services.BooksService;

@Slf4j
@Component
public class BookValidator implements Validator {

    private final BooksService booksService;

    @Autowired
    public BookValidator(BooksService booksService) {
        this.booksService = booksService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.info("Start method validate(target, errors) for BookValidator, target is: {} ", target);
        Book book = (Book) target;

        if (booksService.show(book.getTitle()).isPresent()) {
            if (booksService.show(book.getTitle()).get().getBookId() != book.getBookId()){
                errors.rejectValue("title", "", "Книга с таким названием уже существует");
            }
        }

    }

}
