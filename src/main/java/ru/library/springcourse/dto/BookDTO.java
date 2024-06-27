package ru.library.springcourse.dto;

import ru.library.springcourse.models.Person;

import javax.persistence.Column;
import javax.validation.constraints.*;

public class BookDTO {

    @NotNull(message = "Год издания книги обязателен для заполнения")
    @Min(value = 1700, message = "Год издания книги должен быть больше 1900")
    private Integer yearOfRealise;

    @NotEmpty(message = "Название книги обязательно для заполнения")
    @Size(min = 1, max = 100, message = "Название книги должно содержать от 1 до 100 символов")
    @Pattern(regexp = "[А-ЯЁ].+", message = "Название книги должно начинаться с заглавной буквы: Основы программирования")
    private String title;

    @NotEmpty(message = "Автор обязателен для заполнения")
    @Size(min = 5, max = 50, message = "Автор должен содержать от 5 до 50 символов")
    @Pattern(regexp = "[А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+", message = "Автор должен быть следующего формата: Фамилия Имя")
    private String author;

    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Integer getYearOfRealise() {
        return yearOfRealise;
    }

    public void setYearOfRealise(Integer yearOfRealise) {
        this.yearOfRealise = yearOfRealise;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
