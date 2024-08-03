package ru.library.springcourse.resource;

import io.swagger.annotations.ApiParam;
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

    @Operation(summary = "Вход на страницу админа", description = "Необходимо иметь роль \"ROLE_ADMIN\"")
    @GetMapping("/admin")
    String adminPage();

    @Operation(
            summary = "Список всех читателей",
            description = "Отправка запроса на получение всех читателей"
    )
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

    @Operation(
            summary = "Список всех книг",
            description = "Отправка запроса на получение всех книг в библиотеке: " +
            "\n - с возможностью сортировки по году издания;" +
            "\n - с возможностью выбора номера страницы;" +
            "\n - с возможностью выбора количества книг на странице."
    )
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
    BookResponse books(@ApiParam(value = "Возможность сортировки книг по году издания") @RequestParam(value = "isSortedByYear", required = false) Boolean isSortedByYear,
                       @ApiParam(value = "Номер выводимой страницы") @RequestParam(value = "page", required = false) Integer page,
                       @ApiParam(value = "Количество книг на странице") @RequestParam(value = "limitOfBooks", required = false) Integer limitOfBooks);

    @Operation(
            summary = "Вывод отсортированного списка страниц. " +
            "\n(Использовал отдельную кнопку при работе с Thymeleaf)"
    )
    @GetMapping("/sortedByYear")
    BookResponse sortedBooksByYear();

    @Operation(
            summary = "Добавление новой книги",
            description = "Необходимо ввести корректные данне новой книги," +
            " которые соответствуют параметрам." +
            "\n Смотри ограничения во вкладке \"Model\""
    )
    @PostMapping("/newBook")
    ResponseEntity newBook(@RequestBody @Valid BookDTO bookDTO, BindingResult bindingResult);

    @Operation(
            summary = "Получение книги по ее идентификационному номеру",
            description = "Необходимо отправить корректный идентификатор книги."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - Книги с таким id не найдено")
    })
    @GetMapping("/books/{id}")
    @ResponseBody
    ResponseEntity<BookDTO> showBook(
            @Parameter(name = "id", description = "Идентификатор книги для запроса на её получение", example = "1") @PathVariable("id") int id
    );

    @Operation(
            summary = "Редактирование данных книги",
            description = "Необходимо отправить корректный идентификатор книги."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - Книги с таким id не найдено")
    })
    @PatchMapping("/books/{id}")
    ResponseEntity updateBook(@RequestBody @Valid BookDTO bookDTO, BindingResult bindingResult,
                              @ApiParam(value = "Идентификатор книги", example = "3") @PathVariable("id") int id);

    @Operation(
            summary = "Освободить книгу",
            description = "Необходимо отправить корректный идентификатор книги."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - Книги с таким id не найдено")
    })
    @PatchMapping("/books/{id}/makeFree")
    ResponseEntity makeBookFree(@ApiParam(value = "Идентификатор книги", example = "3") @PathVariable("id") int id);

    @Operation(
            summary = "Закрепить книгу за читателем",
            description = "Необходимо отправить корректные идентификаторы книги и читателя."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - Книги с таким id не найдено/ Читателя с таким id не найдено")
    })
    @PatchMapping("/books/{bookId}/{personId}/assignPerson")
    ResponseEntity assignPerson(
            @ApiParam(value = "Идентификатор книги", example = "1") @PathVariable("bookId") int bookId,
            @ApiParam(value = "Идентификатор читателя", example = "2") @PathVariable("personId") int personId);

    @DeleteMapping("/books/{id}")
    ResponseEntity deleteBook(@PathVariable("id") int id);

    @GetMapping("/books/search")
    ResponseEntity search(@RequestParam(value = "searchBook", required = false, defaultValue = "") String searchBook);

    @Operation(
            summary = "Получение читателя по его идентификационному номеру",
            description = "Необходимо отправить корректный идентификатор читателя."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - Читателя с таким id не найдено")
    })
    @GetMapping("/people/{id}")
    ResponseEntity show(@PathVariable("id") int id);

    @Operation(
            summary = "Редактирование данных читателя",
            description = "Необходимо отправить корректный идентификатор читателя."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - Читателя с таким id не найдено")
    })
    @PatchMapping("/people/{id}")
    ResponseEntity updatePerson(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult,
                                @PathVariable("id") int id);

    @DeleteMapping("/people/{id}")
    ResponseEntity deletePerson(@PathVariable("id") int id);
}
