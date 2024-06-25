package ru.library.springcourse.dto;

import javax.validation.constraints.*;

public class PersonDTO {
    @NotEmpty(message = "ФИО обязательно для заполнения")
    @Size(min = 8, max = 100, message = "ФИО должно содержать от 8 до 100 символов")
    @Pattern(regexp = "[А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+", message = "ФИО должно соответствовать следующему формату: Фамилия Имя Отчество")
    private String fullName;


    @Min(value = 1900, message = "Год рождения должен быть больше 1900г.")
    @NotNull(message = "Год рождения не должен быть пустым")
    private Integer yearOfBirthday;

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
