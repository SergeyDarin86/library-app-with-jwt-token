package ru.library.springcourse.controllers;

//import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.library.springcourse.dto.BookDTO;
import ru.library.springcourse.models.Book;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.services.AdminService;
import ru.library.springcourse.services.BooksService;
import ru.library.springcourse.services.PeopleService;
import ru.library.springcourse.util.*;


import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/library")
public class BookController {

    //TODO:
    //TODO: 1) сделать ExceptionHandler для обработки ошибок (вернуть пользователю)
    //TODO: 2) сделать везде RestController для работы с библиотекой через Postman
    //TODO: 3) Сделать отдельные классы PeopleResponse и BookResponse, чтобы не возвращать List<DTO> в контроллере
    private final PersonValidator personValidator;

    private final BookValidator bookValidator;

    private final PeopleService peopleService;

    private final BooksService booksService;

    private final AdminService adminService;

    @Autowired
    public BookController(PersonValidator personValidator, BookValidator bookValidator, PeopleService peopleService, BooksService booksService, AdminService adminService) {
        this.personValidator = personValidator;
        this.bookValidator = bookValidator;
        this.peopleService = peopleService;
        this.booksService = booksService;
        this.adminService = adminService;
    }

    @GetMapping("/admin")
    public String adminPage() {
        adminService.doAdmin();
        return "/people/adminPage";
    }

    @GetMapping("/people")
    public PersonResponse people() {
        return peopleService.allPeople();
    }

    @GetMapping("/books")
    @ResponseBody
    public BookResponse books(@RequestParam(value = "isSortedByYear", required = false) Boolean isSortedByYear,
                            @RequestParam(value = "page", required = false) Integer page,
                            @RequestParam(value = "limitOfBooks", required = false) Integer limitOfBooks) {
        return booksService.getAllBooks(page,limitOfBooks,isSortedByYear);
    }

    // ендпоинт для кнопки сортировки при использовании Thymeleaf
    @GetMapping("/sortedByYear")
    @ResponseBody
    public List<Book> sortedBooks() {
        return booksService.sortedBooks();
    }

    @PostMapping("/newBook")
    public ResponseEntity newBook(@RequestBody @Valid BookDTO bookDTO, BindingResult bindingResult) {
        bookValidator.validate(booksService.convertToBookFromDTO(bookDTO), bindingResult);
        ExceptionBuilder.buildErrorMessageForClient(bindingResult);

        booksService.save(booksService.convertToBookFromDTO(bookDTO));

        return ResponseEntity.ok(bookDTO);
    }

    @GetMapping("/books/{id}")
    @ResponseBody
    public ResponseEntity showBook(@PathVariable("id") int id) {
        Book book = booksService.show(id);

        if (book == null) {
            ExceptionBuilder.buildErrorMessageForClientBookIdNotFound(id, book);
        }
        return ResponseEntity.ok(booksService.convertToDTOFromBook(booksService.show(id)));
    }

    //TODO: посмотреть, как улучшить логику в данном методе
    // возможно нужно вынести логику в сервис

    @PatchMapping("/books/{id}")
    public ResponseEntity updateBook(@RequestBody @Valid BookDTO bookDTO, BindingResult bindingResult,
                                     @PathVariable("id") int id) {
        ExceptionBuilder.buildErrorMessageForClientBookIdNotFound(id, booksService.show(id));
        Book convertedBook = booksService.convertToBookFromDTO(bookDTO);
        convertedBook.setBookId(id);

        bookValidator.validate(convertedBook, bindingResult);
        ExceptionBuilder.buildErrorMessageForClient(bindingResult);

        booksService.update(id, booksService.convertToBookFromDTO(bookDTO));
        return ResponseEntity.ok(bookDTO);
    }

    @PatchMapping("/books/{id}/makeFree")
    public ResponseEntity makeBookFree(@PathVariable("id") int id) {
        ExceptionBuilder.buildErrorMessageForClientBookIdNotFound(id, booksService.show(id));
        booksService.makeBookFree(id);

        return ResponseEntity.ok(booksService.convertToDTOFromBook(booksService.show(id)));
    }

    //изменить на "PatchMapping"
    @GetMapping("/books/{id}/assignPerson")
    public String assignPerson(@PathVariable("id") int id, @ModelAttribute("person") Person person) {
        booksService.assignPerson(id, person.getPersonId());
        return "redirect:/library/books/{id}";
    }

    @DeleteMapping("/books/{id}")
    public String deleteBook(@PathVariable("id") int id) {
        booksService.delete(id);
        return "redirect:/library/books";
    }

    @GetMapping("/books/search")
    public String search(@RequestParam(value = "searchBook", required = false, defaultValue = "") String searchBook,
                         Model books, Model optionalPersonWithBook) {

        if (searchBook != null && !searchBook.equals("")) {
            books.addAttribute("books", booksService.getBookListByTitleStartingWith(searchBook));
            if (booksService.getBookListByTitleStartingWith(searchBook) != null)
                optionalPersonWithBook.addAttribute("peopleService", peopleService);
        } else {
            books.addAttribute("books", new ArrayList<Book>() {
            });
            optionalPersonWithBook.addAttribute("optionalPersonWithBook", Optional.empty());
        }

        return "books/searchBook";
    }


//    @GetMapping("/newPerson")
//    public String newPerson(@ModelAttribute Person person) {
//        return "people/newPerson";
//    }

    //TODO: переделать данный метод под регистрацию с логином и паролем
    // добавить новые поля в форму.
    // Если регистрация прошла успешно, то перенаправлять на страницу "/auth/login"
    // добавить валидацию на наличие пользователя с вводимым логином(userName) в базе
    // если такой пользователь уже существует, то делаем return на эту же страницу
    // посмотреть, как будет происходить редактирование пользователя
    // (возможно нужно снова добавлять скрытые поля "login", "password" в форму редактирования)

//    @PostMapping()
//    public String create(@ModelAttribute("person") @Valid Person person
//            , BindingResult bindingResult) {
//
//        personValidator.validate(person, bindingResult);
//
//        if (bindingResult.hasErrors())
//            return "people/newPerson";
//
//        peopleService.save(person);
//        return "redirect:/library/people";
//    }

    @GetMapping("/people/{id}")
    public String show(@PathVariable("id") int id, Model model, Model modelBook) {
        model.addAttribute("person", peopleService.show(id));
        modelBook.addAttribute("books", booksService.findAllBooksByPerson(peopleService.show(id)));
        return "people/showPerson";
    }

    @GetMapping("/people/{id}/edit")
    public String editPerson(Model model, @PathVariable("id") int id) {
        model.addAttribute("person", peopleService.show(id));
        return "people/editPerson";
    }

    @PatchMapping("/people/{id}")
    public String updatePerson(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
                               @PathVariable("id") int id) {

        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            return "people/editPerson";

        peopleService.update(id, person);
        return "redirect:/library/people";
    }

    @DeleteMapping("/people/{id}")
    public String deletePerson(@PathVariable("id") int id) {
        peopleService.delete(id);
        return "redirect:/library/people";
    }

    @ExceptionHandler
    private ResponseEntity<LibraryErrorResponse> measurementHandlerException(LibraryException e) {
        LibraryErrorResponse response = new LibraryErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<LibraryErrorResponse> measurementHandlerException(LibraryExceptionNotFound e) {
        LibraryErrorResponse response = new LibraryErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
