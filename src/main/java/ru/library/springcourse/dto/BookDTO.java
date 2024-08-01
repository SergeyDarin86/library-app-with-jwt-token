package ru.library.springcourse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.library.springcourse.models.Person;

import javax.validation.constraints.*;

@Schema(description = "DTO для сущности \"Book\"")
public class BookDTO {

    @Schema(description = "Год издания книги", example = "1990")
    @NotNull(message = "Год издания книги обязателен для заполнения")
    @Min(value = 1700, message = "Год издания книги должен быть больше 1700")
    private Integer yearOfRealise;

    @Schema(description = "Заголовок книги", example = "Том Сойер")
    @NotEmpty(message = "Название книги обязательно для заполнения")
    @Size(min = 1, max = 100, message = "Название книги должно содержать от 1 до 100 символов")
    @Pattern(regexp = "[А-ЯЁ].+", message = "Название книги должно начинаться с заглавной буквы: Основы программирования")
    private String title;

    @Schema(description = "Автор книги", example = "Марк Твен")
    @NotEmpty(message = "Автор обязателен для заполнения")
    @Size(min = 5, max = 50, message = "Автор должен содержать от 5 до 50 символов")
    @Pattern(regexp = "[А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+", message = "Автор должен быть следующего формата: Фамилия Имя")
    private String author;

    private PersonDTO personDTO;

    public PersonDTO getPerson() {
        return personDTO;
    }

    public void setPerson(PersonDTO person) {
        this.personDTO = person;
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

    @Override
    public String toString() {
        return "BookDTO{" +
                "yearOfRealise=" + yearOfRealise +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", person=" + personDTO +
                '}';
    }
}
