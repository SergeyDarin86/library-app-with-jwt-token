package ru.library.springcourse.securuty;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.library.springcourse.models.Person;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Sergey D.
 */

// Это класс обертка над нашей сущностью. Он предоставляет детали о пользователе
// должен реализовывать интерфейс UserDetails
// В Spring Security не принято, чтобы напрямую обращались к сущности для того, чтобы получать поля

public class PersonDetails implements UserDetails {

    private final Person person;

    public PersonDetails(Person person) {
        this.person = person;
    }

    // возвращаем роль человека или список действий, которые он может совершать
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(person.getRole()));    //получаем роли, которые есть у пользователя (реализуем позже)
    }

    @Override
    public String getPassword() {
        return this.person.getPassword();
    }

    @Override
    public String getUsername() {
        return this.person.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;    //не просрочен
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;    //не заблокирован
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;   // не просрочен
    }

    @Override
    public boolean isEnabled() {
        return true;   // включен, работает
    }

    // нужно, чтобы получать данные аутентифицированного пользователя
    public Person getPerson(){
        return this.person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDetails that = (PersonDetails) o;
        return Objects.equals(person, that.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person);
    }
}
