package ru.library.springcourse.models;

//import jakarta.persistence.*;
//import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "person")
public class Person {

    //!!! Быть внимательным к именованию полей сущностей!!!
    // если в базе поле записано как person_id, то в сущности лучше именовать с использованием Camel
    // и дать название для поля как personId, а не просто "id"
    // в противном случае не получается полноценно извлечь значение поля и оно для всех будет равно 0
    // долго искал причину, почему приходит из запроса "Select * from Person" значения для id для всех
    // записей было равно 0 (((
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private int personId;

    // на данный момент regex для кириллицы
    @NotEmpty(message = "ФИО обязательно для заполнения")
    @Size(min = 8, max = 100, message = "ФИО должно содержать от 8 до 100 символов")
    @Pattern(regexp = "[А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+", message = "ФИО должно соответсвовать следующему формату: Фамилия Имя Отчество")
    @Column(name = "full_name")
    private String fullName;


    //TODO: посмотреть как настроить Pattern для числового поля, чтобы вводить только 4 символа для г.р.

    @Min(value = 1900, message = "Год рождения должен быть больше 1900г.")
    @NotNull(message = "Год рождения не должен быть пустым")
    @Column(name = "year_of_birthday")
    private Integer yearOfBirthday;

    @OneToMany(mappedBy = "person")
    @Cascade(value = {
            org.hibernate.annotations.CascadeType.PERSIST,
            org.hibernate.annotations.CascadeType.MERGE,
    org.hibernate.annotations.CascadeType.REFRESH})
    private List<Book>books;

    @Column(name = "login")
    @NotEmpty(message = "Логин не должен быть пустым")
    private String login;

    @Column(name = "password")
    @NotEmpty(message = "Пароль не должен быть пустым")
    private String password;

    @Column(name = "role")
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @JsonIgnore
    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    // конструктор по умолчанию нужен для Spring
    public Person() {
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
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

    @Override
    public String toString() {
        return "{fullName=" + fullName + ", yearOfBirthday=" + yearOfBirthday + ", login=" + login + ", role=" + role +"}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return personId == person.personId && Objects.equals(fullName, person.fullName) && Objects.equals(yearOfBirthday, person.yearOfBirthday) && Objects.equals(books, person.books) && Objects.equals(login, person.login) && Objects.equals(password, person.password) && Objects.equals(role, person.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personId, fullName, yearOfBirthday, books, login, password, role);
    }
}
