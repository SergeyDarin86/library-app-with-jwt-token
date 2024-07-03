package ru.library.springcourse.services;

import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.PeopleRepository;

import static org.mockito.Mockito.*;

class RegistrationServiceTest extends TestCase {

    Person person = new Person();

    RegistrationService registrationService = Mockito.mock(RegistrationService.class);

    PeopleRepository peopleRepository = Mockito.mock(PeopleRepository.class);

    PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

    @Override
    @BeforeEach
    protected void setUp() {
        person.setPersonId(1);
        person.setFullName("Дарин Сергей Владимирович");
        person.setRole("ROLE_USER");
        person.setPassword(passwordEncoder.encode("password"));
    }

    @Test
    void register() {
        registrationService.register(person);
        verify(registrationService,times(1)).register(person);
        doNothing().when(registrationService).register(person);

        when(peopleRepository.save(person)).thenReturn(person);
        Person actualPerson = peopleRepository.save(person);
        assertEquals(person,actualPerson);
        verify(peopleRepository,times(1)).save(person);
    }
}