package ru.library.springcourse.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Date;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private int bookId;

    @NotNull(message = "Year of Realise should not be empty")
    @Min(value = 1, message = "Year of Realise should be more than 0")
//    @Pattern(regexp = "\\d{4}",message = "The format has to be: (4 numbers)")

    @Column(name = "year_of_realise")
    private Integer yearOfRealise;

    @NotEmpty(message = "Title of Book should not be empty")
    @Size(min = 1, max = 100, message = "Title should be between 1 and 100 characters")
    @Pattern(regexp = "[А-ЯЁ].+", message = "Title book should have format: Apple")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Author should not be empty")
    @Size(min = 5, max = 50, message = "Author should be between 5 and 50 characters")
    @Pattern(regexp = "[А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+", message = "The Author should have the format: Surname Name")
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
