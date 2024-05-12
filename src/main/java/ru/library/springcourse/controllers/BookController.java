package ru.library.springcourse.controllers;

//import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.library.springcourse.models.Book;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.services.BooksService;
import ru.library.springcourse.services.PeopleService;
import ru.library.springcourse.util.BookValidator;
import ru.library.springcourse.util.PersonValidator;


import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Optional;

@Controller
@RequestMapping("/library")
public class BookController {

    private final PersonValidator personValidator;

    private final BookValidator bookValidator;

    private final PeopleService peopleService;

    private final BooksService booksService;

    @Autowired
    public BookController(PersonValidator personValidator, BookValidator bookValidator, PeopleService peopleService, BooksService booksService) {
        this.personValidator = personValidator;
        this.bookValidator = bookValidator;
        this.peopleService = peopleService;
        this.booksService = booksService;
    }

    @GetMapping("/people")
    public String people(Model model) {
        model.addAttribute("people", peopleService.allPeople());
        return "people/readers";
    }

    @GetMapping("/books")
    public String books(Model model, @RequestParam(value = "isSortedByYear", required = false) Boolean isSortedByYear,
                        @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "limitOfBooks", required = false) Integer limitOfBooks) {
        model.addAttribute("books", booksService.findAll(page, limitOfBooks, isSortedByYear));
        return "books/books";
    }

    // ендпоинт для кнопки сортировки
    @GetMapping("/sortedByYear")
    public String sortedBooks(Model model, @RequestParam(value = "isSortedByYear", required = false) Boolean isSortedByYear) {
        model.addAttribute("books", booksService.sortedBooks());
        return "books/books";
    }

    @GetMapping("/newBook")
    public String newBook(@ModelAttribute Book book) {
        return "books/newBook";
    }

    @PostMapping("/books")  // new parameter
    public String createBook(@ModelAttribute("book") @Valid Book book
            , BindingResult bindingResult) {

        bookValidator.validate(book, bindingResult);

        if (bindingResult.hasErrors())
            return "books/newBook";
        booksService.save(book);

        return "redirect:/library/books";
    }

    @GetMapping("/books/{id}")
    public String showBook(@PathVariable("id") int id, Model model, Model optionalModel,
                           Model modelAddPerson, @ModelAttribute("person") Person person) {

        model.addAttribute("book", booksService.show(id));
        optionalModel.addAttribute("optionalPerson", peopleService.findPersonByBookId(id));
        modelAddPerson.addAttribute("people", peopleService.allPeople());

        return "books/showBook";
    }

    @GetMapping("/books/{id}/edit")
    public String editBook(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", booksService.show(id));
        return "books/editBook";
    }

    // чтобы Spring смог читать значение скрытого поля (_method) необходимо использовать фильтр
    @PatchMapping("/books/{id}")
    public String updateBook(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                             @PathVariable("id") int id) {
        bookValidator.validate(book, bindingResult);

        if (bindingResult.hasErrors())
            return "books/editBook";

        booksService.update(id, book);
        return "redirect:/library/books";
    }

    //изменить на "PatchMapping"
    @GetMapping("/books/{id}/makeFree")
    public String makeBookFree(@PathVariable("id") int id) {
        booksService.makeBookFree(id);
        return "redirect:/library/books/{id}";
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

}
