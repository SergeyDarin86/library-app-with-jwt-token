package ru.library.springcourse.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.library.springcourse.models.Book;
import ru.library.springcourse.models.Person;

import java.util.List;

/**
 * Класс, который предназначен для формирования текста ошибки для пользователя
 *
 * @author Sergey D.
 */
public class ExceptionBuilder {

    /**
     * Метод для формирования текста
     *
     * @param bindingResult BindingResult, который перехватывает ошибку при валидации данных
     * @throws LibraryException
     */
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

    /**
     * Метод для построения ошибки в случае некорректного идентификационного номера книги
     *
     * @param id   Идентификационный номер книги
     * @param book Экземпляр книги
     * @throws LibraryExceptionNotFound
     */
    public static void buildErrorMessageForClientBookIdNotFound(int id, Book book) {
        if (book == null) {
            String errorMsg = id + " - Книги с таким id не найдено";

            throw new LibraryExceptionNotFound(errorMsg);
        }
    }

    /**
     * Метод для построения ошибки в случае некорректного ввода заголовка книги (поиск)
     *
     * @param bookResponse Экземпляр BookResponse
     * @throws LibraryExceptionNotFound
     */
    public static void buildErrorMessageForClientBookNotFound(BookResponse bookResponse) {
        if (bookResponse.getBookDTOList().isEmpty()) {
            String errorMsg = "Не найдено соответствий по заголовку";

            throw new LibraryExceptionNotFound(errorMsg);
        }
    }

    /**
     * Метод для построения ошибки в случае некорректного идентификационного номера читателя
     *
     * @param id     Идентификационный номер читателя
     * @param person Экземпляр читателя
     * @throws LibraryExceptionNotFound
     */
    public static void buildErrorMessageForClientPersonIdNotFound(int id, Person person) {
        if (person == null) {
            String errorMsg = id + " - Человека с таким id не найдено";

            throw new LibraryExceptionNotFound(errorMsg);
        }
    }

    /**
     * Метод для построения ошибки в случае некорректного ввода заголовка книги (поиск по частичному совпадению)
     *
     * @param searchedBook Начальные буквы заголовка книги
     * @throws LibraryExceptionNotFound
     */
    public static void buildErrorMessageForClientTitleNotEntered(String searchedBook) {
        if (searchedBook == null || searchedBook.equals("")) {
            String errorMsg = " Введите поисковый запрос";

            throw new LibraryExceptionNotFound(errorMsg);
        }
    }

    /**
     * Метод для построения ошибки в случае назначении книги, которая уже в пользовании
     *
     * @param book Экземпляр книги
     * @throws LibraryExceptionNotAcceptable
     */
    public static void buildErrorMessageForClientBookAlreadyIsUsed(Book book) {
        if (book.getPerson() != null) {
            String errorMsg = " Невозможно назначить книгу - книга уже в пользовании";

            throw new LibraryExceptionNotAcceptable(errorMsg);
        }
    }

}
