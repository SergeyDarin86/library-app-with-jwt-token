package ru.library.springcourse.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.PeopleRepository;
import ru.library.springcourse.securuty.PersonDetails;

import java.util.Optional;

// это не совсем обычный сервис. Предназначен для Spring Security
// даем понять Spring Security, что мы можем получать пользователя здесь

@Service
public class PersonDetailsService implements UserDetailsService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PersonDetailsService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    // любой объект, который реализует класс UserDetails, может быть возвращен в этом методе
    // наш класс PersonDetails как раз имплементирует UserDetails
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Person>optionalPerson = peopleRepository.findPersonByLogin(username);
        if (optionalPerson.isEmpty())
            // это исключение ловится Spring Security
            throw new UsernameNotFoundException("Такой пользователь не найден");

        // оборачиваем человека в класс PersonDetails
        return new PersonDetails(optionalPerson.get());
    }
}
