package ru.library.springcourse.resource;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.library.springcourse.config.SwaggerConfig;
import ru.library.springcourse.dto.AuthenticationDTO;
import ru.library.springcourse.dto.PersonDTO;
import ru.library.springcourse.util.LibraryErrorResponse;

import javax.validation.Valid;
import java.util.Map;

@Api(tags = {SwaggerConfig.TAG_AUTH_CONTROLLER})
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
    @ApiResponse(
            responseCode = "400",
            description = "Incorrect Credentials",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema (
                                    schema = @Schema(implementation = LibraryErrorResponse.class,
                                    discriminatorMapping = {
                                            @DiscriminatorMapping(value = "esfdsf", schema = LibraryErrorResponse.class)
                                    })))
            })
    ResponseEntity performRegistration(@RequestBody @Valid PersonDTO personDTO
            , BindingResult bindingResult);

    @Operation(summary = "Данные о пользователе", description = "Отправка запроса для получения данных о пользователе (получаем логин)")
    @ApiResponse(
            responseCode = "200",
            description = "Метод успешно выполнен.",
            content = {
                    @Content(
                            mediaType = "Text",
                            array = @ArraySchema(schema = @Schema(implementation = String.class,defaultValue = "String")))
            })
    @ApiResponse(
            responseCode = "405",
            description = "Method not allowed")
    @SecurityRequirement(name = "JWT")

    @GetMapping
    String showUserInfo();
}
