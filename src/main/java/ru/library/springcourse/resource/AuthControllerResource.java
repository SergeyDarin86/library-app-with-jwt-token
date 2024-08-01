package ru.library.springcourse.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import ru.library.springcourse.dto.AuthenticationDTO;
import ru.library.springcourse.dto.PersonDTO;

import javax.validation.Valid;
import java.util.Map;

@Tag(name = "Api сервиса Аутентификации",
        description = "Сервис предназначен для регистрации читателей")
public interface AuthControllerResource {

    @Operation(summary = "Вход в систему", description = "Необходимо ввести корректные данные (логин, пароль)")
    @ApiResponse(
            responseCode = "200",
            description = "Метод успешно выполнен.")
    Map<String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO);

    @Operation(summary = "Регистрация нового пользователя", description = "Логин и ФИО пользователя должны быть уникальны")
    @ApiResponse(
            responseCode = "200",
            description = "Метод успешно выполнен.",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class)))
            })
    ResponseEntity performRegistration(@RequestBody @Valid PersonDTO personDTO
            , BindingResult bindingResult);

    @Operation(summary = "Данные о пользователе", description = "Отправка запроса для получения данных о пользователе (получаем логин)")
    @ApiResponse(
            responseCode = "200",
            description = "Метод успешно выполнен.",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class)))
            })
    @ApiResponse(
            responseCode = "405",
            description = "Method not allowed")

    String showUserInfo();
}
