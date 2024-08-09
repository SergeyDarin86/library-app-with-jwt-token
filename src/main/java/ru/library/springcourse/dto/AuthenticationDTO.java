package ru.library.springcourse.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;

@ApiModel(description = "DTO для аутентификации пользователей")
public class AuthenticationDTO {

    @ApiModelProperty(notes = "Логин пользователя", example = "user123", required = true)
    @NotEmpty(message = "Логин не должен быть пустым")
    private String login;

    @ApiModelProperty(notes = "Пароль пользователя", example = "11s345d78!", required = true)
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
