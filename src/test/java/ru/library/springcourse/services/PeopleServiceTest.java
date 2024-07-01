package ru.library.springcourse.services;

import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.PeopleRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PeopleServiceTest extends TestCase {

    Person person = new Person();

    PeopleRepository repository = Mockito.mock(PeopleRepository.class);

    PeopleService peopleService = Mockito.mock(PeopleService.class);

    @Override
    @BeforeEach
    protected void setUp(){
        person.setPersonId(1);
        person.setFullName("Дарин Сергей Владимирович");
        person.setYearOfBirthday(1986);
    }

    @Test
    void show() {
        when(peopleService.show(1)).thenReturn(person);
        assertEquals(person,peopleService.show(1));

        when(repository.findById(1)).thenReturn(Optional.of(person));
        Person expectedPerson = repository.findById(1).get();
        assertEquals(person,expectedPerson);
        verify(repository, times(1)).findById(1);
    }

    @Test
    void showByFullName() {
        when(peopleService.show("Дарин Сергей Владимирович")).thenReturn(Optional.of(person));
        assertEquals(person,peopleService.show("Дарин Сергей Владимирович").get());
        verify(peopleService, times(1)).show("Дарин Сергей Владимирович");

        when(repository.findPersonByFullName("Дарин Сергей Владимирович")).thenReturn(Optional.of(person));
        Person expectedPerson = repository.findPersonByFullName("Дарин Сергей Владимирович").get();
        assertEquals(person,expectedPerson);
        verify(repository, times(1)).findPersonByFullName("Дарин Сергей Владимирович");
    }
}