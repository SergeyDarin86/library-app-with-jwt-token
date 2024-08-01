package ru.library.springcourse.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;

@Schema(description = "DTO для сущности \"Person\"")
public class PersonDTO {

    @Schema(description = "ФИО пользователя", example = "Дарин Сергей Владимирович")
    @NotEmpty(message = "ФИО обязательно для заполнения")
    @Size(min = 8, max = 100, message = "ФИО должно содержать от 8 до 100 символов")
    @Pattern(regexp = "[А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+", message = "ФИО должно соответствовать следующему формату: Фамилия Имя Отчество")
    private String fullName;

    @Schema(description = "Год рождения", example = "1986")
    @Min(value = 1900, message = "Год рождения должен быть больше 1900г.")
    @NotNull(message = "Год рождения не должен быть пустым")
    private Integer yearOfBirthday;

    @Schema(description = "Логин", example = "user")
    @NotEmpty(message = "Логин не должен быть пустым")
    private String login;

    @Schema(description = "Пароль", example = "password")
    @NotEmpty(message = "Пароль не должен быть пустым")
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getYearOfBirthday() {
        return yearOfBirthday;
    }

    public void setYearOfBirthday(Integer yearOfBirthday) {
        this.yearOfBirthday = yearOfBirthday;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
