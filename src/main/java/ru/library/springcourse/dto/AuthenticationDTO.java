package ru.library.springcourse.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

@Schema(description = "DTO для аутентификации пользователей")
public class AuthenticationDTO {

    @Schema(description = "логин", example = "user")
    @NotEmpty(message = "Логин не должен быть пустым")
    private String login;

    @Schema(description = "пароль", example = "123456")
    @NotEmpty(message = "Пароль не должен быть пустым")
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
