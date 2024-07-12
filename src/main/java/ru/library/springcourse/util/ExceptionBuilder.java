package ru.library.springcourse.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.library.springcourse.models.Book;
import ru.library.springcourse.models.Person;

import java.util.List;
import java.util.Optional;

public class ExceptionBuilder {
    public static void buildErrorMessageForClient(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errorList = bindingResult.getFieldErrors();
            errorList.stream().forEach(fieldError -> errorMsg
                    .append(fieldError.getField())
                    .append(" - ").append(fieldError.getDefaultMessage())
                    .append(";"));

            throw new LibraryException(errorMsg.toString());
        }
    }

    public static void buildErrorMessageForClientBookIdNotFound(int id, Book book) {
        if (book == null) {
            String errorMsg = id + " - Книги с таким id не найдено";

            throw new LibraryExceptionNotFound(errorMsg);
        }
    }

    public static void buildErrorMessageForClientBookNotFound(Optional<Book>bookOptional) {
        if (bookOptional.isEmpty()) {
            String errorMsg = "Такой книги не найдено Book not found";

            throw new LibraryExceptionNotFound(errorMsg);
        }
    }

    public static void buildErrorMessageForClientPersonIdNotFound(int id, Person person) {
        if (person == null) {
            String errorMsg = id + " - Человека с таким id не найдено";

            throw new LibraryExceptionNotFound(errorMsg);
        }
    }

    public static void buildErrorMessageForClientTitleNotEntered(String searchedBook) {
        if (searchedBook == null || searchedBook.equals("")) {
            String errorMsg = " Введите поисковый запрос No query";

            throw new LibraryExceptionNotFound(errorMsg);
        }
    }

    public static void buildErrorMessageForClientBookAlreadyIsUsed(Book book) {
        if (book.getPerson() != null) {
            String errorMsg = " Невозможно назначить книгу - книга уже в пользовании";

            throw new LibraryExceptionNotAcceptable(errorMsg);
        }
    }

}
