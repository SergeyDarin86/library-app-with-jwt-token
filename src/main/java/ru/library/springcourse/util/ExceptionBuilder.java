package ru.library.springcourse.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.library.springcourse.models.Book;

import java.util.List;

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

}
