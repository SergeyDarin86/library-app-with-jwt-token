package ru.library.springcourse.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.PeopleRepository;
import ru.library.springcourse.securuty.PersonDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonDetailsServiceTest {

    PeopleRepository peopleRepository = Mockito.mock(PeopleRepository.class);

    PersonDetailsService personDetailsService = Mockito.mock(PersonDetailsService.class);

    Person person = new Person();

    @BeforeEach
    void setUp() {
        person.setPersonId(1);
        person.setLogin("user");
    }

    @Test
    void loadUserByUsername() {
        PersonDetails personDetailsExpected = new PersonDetails(person);

        when(personDetailsService.loadUserByUsername("user")).thenReturn(new PersonDetails(person));
        UserDetails personDetailsActual = personDetailsService.loadUserByUsername("user");
        verify(personDetailsService,times(1)).loadUserByUsername("user");

        assertEquals(personDetailsExpected,personDetailsActual);

        Optional<Person>optionalPersonExpected = Optional.of(person); // для исключения поставить null

        Person actualPerson = new Person();
        actualPerson.setPersonId(1);
        actualPerson.setLogin("user");

        when(peopleRepository.findPersonByLogin("user")).thenReturn(Optional.of(actualPerson));
        Optional<Person>personOptionalActual = peopleRepository.findPersonByLogin("user");
        verify(peopleRepository,times(1)).findPersonByLogin("user");
        assertEquals(optionalPersonExpected,personOptionalActual);
    }

    @Test()
    void loadUserByUsernameShouldThrowException() throws UsernameNotFoundException{
        when(personDetailsService.loadUserByUsername(anyString())).thenThrow(UsernameNotFoundException.class);
    }

}