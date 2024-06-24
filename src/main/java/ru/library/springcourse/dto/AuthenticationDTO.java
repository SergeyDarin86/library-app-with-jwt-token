package ru.library.springcourse.dto;

import javax.validation.constraints.NotEmpty;

public class AuthenticationDTO {

    @NotEmpty(message = "Логин не должен быть пустым")
    private String login;

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
