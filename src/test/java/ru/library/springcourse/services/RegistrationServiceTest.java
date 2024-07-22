package ru.library.springcourse.services;

import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.PeopleRepository;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
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

//    @Test
    void registerNew() {
//        registrationService.register(person);
//        verify(registrationService,times(1)).register(person);
//        doNothing().when(registrationService).register(person);

        Person person1 = Mockito.mock(Person.class);
        person1.setRole("ROLE_USER");
        Person spy = spy(person1);

        spy.setRole("ROLE_USER");
        System.out.println(spy.getRole());

        verify(spy).setRole("ROLE_USER");
        doReturn("ROLE_USER").when(spy).getRole();

        when(peopleRepository.save(spy)).thenReturn(person1);



//        assertNotNull(person.getRole());
//        assertEquals("ROLE_USER",person.getRole());
//        when(peopleRepository.save(person)).thenReturn(person);
//        Person actualPerson = peopleRepository.save(person);
//        assertEquals(person,actualPerson);
//        verify(peopleRepository,times(1)).save(person);
    }

}