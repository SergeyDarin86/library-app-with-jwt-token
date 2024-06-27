package ru.library.springcourse.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.library.springcourse.dto.BookDTO;
import ru.library.springcourse.dto.PersonDTO;
import ru.library.springcourse.models.Book;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.services.AdminService;
import ru.library.springcourse.services.BooksService;
import ru.library.springcourse.services.PeopleService;
import ru.library.springcourse.util.*;


import javax.validation.Valid;

@RestController
@RequestMapping("/library")
public class BookController {

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
    public BookResponse books(@RequestParam(value = "isSortedByYear", required = false) Boolean isSortedByYear,
                              @RequestParam(value = "page", required = false) Integer page,
                              @RequestParam(value = "limitOfBooks", required = false) Integer limitOfBooks) {
        return booksService.getAllBooks(page, limitOfBooks, isSortedByYear);
    }

    // ендпоинт для кнопки сортировки при использовании Thymeleaf
    @GetMapping("/sortedByYear")
    public BookResponse sortedBooksByYear() {
        return booksService.sortedBooksByYear();
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

    //TODO: попробовать изменить логику - если книга уже занята, то выводить соответствующее сообщение
    // "Невозможно назначить книгу - книга уже в пользовании"

    @PatchMapping("/books/{bookId}/{personId}/assignPerson")
    public ResponseEntity assignPerson(@PathVariable("bookId") int bookId, @PathVariable("personId") int personId) {
        ExceptionBuilder.buildErrorMessageForClientBookIdNotFound(bookId, booksService.show(bookId));
        ExceptionBuilder.buildErrorMessageForClientPersonIdNotFound(personId, peopleService.show(personId));

        booksService.assignPerson(bookId, personId);
        return ResponseEntity.ok(peopleService.convertToDTOFromPerson(booksService.show(bookId).getPerson()));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity deleteBook(@PathVariable("id") int id) {
        ExceptionBuilder.buildErrorMessageForClientBookIdNotFound(id, booksService.show(id));

        booksService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/books/search")
    public ResponseEntity search(@RequestParam(value = "searchBook", required = false, defaultValue = "") String searchBook) {

        ExceptionBuilder.buildErrorMessageForClientTitleNotEntered(searchBook);
        ExceptionBuilder.buildErrorMessageForClientBookNotFound(booksService.show(searchBook));
        return ResponseEntity.ok(booksService.getBookListByTitleStartingWith(searchBook));

    }

    @GetMapping("/people/{id}")
    public ResponseEntity show(@PathVariable("id") int id) {
        ExceptionBuilder.buildErrorMessageForClientPersonIdNotFound(id, peopleService.show(id));
        return ResponseEntity.ok(peopleService.show(id));
    }

    @PatchMapping("/people/{id}")
    public ResponseEntity updatePerson(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult,
                                       @PathVariable("id") int id) {

        ExceptionBuilder.buildErrorMessageForClientPersonIdNotFound(id, peopleService.show(id));
        Person convertedPerson = peopleService.convertToPersonFromDTO(personDTO);
        convertedPerson.setPersonId(id);

        personValidator.validate(convertedPerson, bindingResult);
        ExceptionBuilder.buildErrorMessageForClient(bindingResult);

        peopleService.update(id, convertedPerson);
        personDTO.setPassword(convertedPerson.getPassword());
        return ResponseEntity.ok(personDTO);
    }

    @DeleteMapping("/people/{id}")
    public ResponseEntity deletePerson(@PathVariable("id") int id) {
        ExceptionBuilder.buildErrorMessageForClientPersonIdNotFound(id, peopleService.show(id));

        peopleService.delete(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler
    private ResponseEntity<LibraryErrorResponse> libraryHandlerException(LibraryException e) {
        LibraryErrorResponse response = new LibraryErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<LibraryErrorResponse> libraryHandlerException(LibraryExceptionNotFound e) {
        LibraryErrorResponse response = new LibraryErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
