package ru.library.springcourse.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.library.springcourse.dto.BookDTO;
import ru.library.springcourse.dto.PersonDTO;
import ru.library.springcourse.util.BookResponse;
import ru.library.springcourse.util.PersonResponse;

import javax.validation.Valid;

@Tag(name = "Api сервиса библиотеки",
        description = "Сервис предназначен для:" +
                " редактирования и удаления данных о читателях;" +
                " регистрации новых книг, редактировании и удалении данных об устаревших книгах; " +
                " назначения книг читателям и освобождения книг.")
public interface LibraryResource {
    @GetMapping("/admin")
    String adminPage();

    @Operation(description = "Отправка запроса на получение всех читателей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Метод успешно выполнен.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PersonResponse.class)))
                    }),
            @ApiResponse(
                    responseCode = "403",
                    description = "Авторизация не пройдена. Для использования метода необходимо авторизоваться.",
                    content = {
                            @Content(schema = @Schema(implementation = void.class))
                    })
    })
    @GetMapping("/people")
    PersonResponse people();

    @Operation(description = "Отправка запроса на получение всех книг в библиотеке")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Метод успешно выполнен.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = BookResponse.class)))
                    }),
            @ApiResponse(
                    responseCode = "403",
                    description = "Авторизация не пройдена. Для использования метода необходимо авторизоваться.",
                    content = {
                            @Content(schema = @Schema(implementation = void.class))
                    })
    })
    @GetMapping("/books")
    BookResponse books(@RequestParam(value = "isSortedByYear", required = false) Boolean isSortedByYear,
                              @RequestParam(value = "page", required = false) Integer page,
                              @RequestParam(value = "limitOfBooks", required = false) Integer limitOfBooks);

    @GetMapping("/sortedByYear")
    public BookResponse sortedBooksByYear();

    @PostMapping("/newBook")
    ResponseEntity newBook(@RequestBody @Valid BookDTO bookDTO, BindingResult bindingResult);

    @Operation(description = "Отправка запроса на получение книги по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - Книги с таким id не найдено")
    })
    @GetMapping("/books/{id}")
    @ResponseBody
    ResponseEntity<BookDTO> showBook( @Parameter(name = "id", description = "Идентификатор книги для запроса на её получение", example = "1")@PathVariable("id") int id);

    @PatchMapping("/books/{id}")
    ResponseEntity updateBook(@RequestBody @Valid BookDTO bookDTO, BindingResult bindingResult,
                                     @PathVariable("id") int id);

    @PatchMapping("/books/{id}/makeFree")
    ResponseEntity makeBookFree(@PathVariable("id") int id);

    @PatchMapping("/books/{bookId}/{personId}/assignPerson")
    ResponseEntity assignPerson(@PathVariable("bookId") int bookId, @PathVariable("personId") int personId);

    @DeleteMapping("/books/{id}")
    ResponseEntity deleteBook(@PathVariable("id") int id);

    @GetMapping("/books/search")
    ResponseEntity search(@RequestParam(value = "searchBook", required = false, defaultValue = "") String searchBook);

    @GetMapping("/people/{id}")
    ResponseEntity show(@PathVariable("id") int id);

    @PatchMapping("/people/{id}")
    ResponseEntity updatePerson(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult,
                                       @PathVariable("id") int id);

    @DeleteMapping("/people/{id}")
    ResponseEntity deletePerson(@PathVariable("id") int id);
}
