package ru.library.springcourse.models;

//import jakarta.persistence.*;
//import jakarta.validation.constraints.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.Date;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private int bookId;

    @NotNull(message = "Год издания книги обязателен для заполнения")
    @Min(value = 1900, message = "Год издания книги должен быть больше 1900")

    @Column(name = "year_of_realise")
    private Integer yearOfRealise;

    @NotEmpty(message = "Название книги обязательно для заполнения")
    @Size(min = 1, max = 100, message = "Название книги должно содержать от 1 до 100 символов")
    @Pattern(regexp = "[А-ЯЁ].+", message = "Название книги должно начинаться с заглавной буквы: Основы программирования")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Автор обязателен для заполнения")
    @Size(min = 5, max = 50, message = "Автор должен содержать от 5 до 50 символов")
    @Pattern(regexp = "[А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+", message = "Автор должен быть следующего формата: Фамилия Имя")
    @Column(name = "author")
    private String author;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "person_id")
    private Person person;

    @Column(name = "taken_at")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date takenAt;

    @Transient
    private Boolean isTakenMoreThan10Days;

    public Boolean getIsTakenMoreThan10Days() {
        return isTakenMoreThan10Days;
    }

    public void setIsTakenMoreThan10Days(Boolean isTakenMoreThan10Days) {
        this.isTakenMoreThan10Days = isTakenMoreThan10Days;
    }

    public Date getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(Date takenAt) {
        this.takenAt = takenAt;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Book() {
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
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
        return "{ title=" + title + " yearOfRealise= " + yearOfRealise + " author= " + author + "}";
    }

}
