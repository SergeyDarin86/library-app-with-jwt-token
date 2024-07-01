package ru.library.springcourse.services;

import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import ru.library.springcourse.dto.PersonDTO;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.PeopleRepository;
import ru.library.springcourse.util.PersonResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class PeopleServiceTest extends TestCase {

    Person person1 = new Person();

    Person person2 = new Person();

    PersonDTO personDTO1 = new PersonDTO();

    PersonDTO personDTO2 = new PersonDTO();

    PeopleRepository repository = Mockito.mock(PeopleRepository.class);

    PeopleService peopleService = Mockito.mock(PeopleService.class);

    ModelMapper modelMapper = Mockito.mock(ModelMapper.class);

    @Override
    @BeforeEach
    protected void setUp(){
        person1.setPersonId(1);
        person1.setFullName("Дарин Сергей Владимирович");
        person1.setYearOfBirthday(1986);
        person1.setLogin("user");

        person2.setPersonId(2);
        person2.setFullName("Иванов Иван Иванович");
        person2.setYearOfBirthday(1987);
    }

    @BeforeEach
    void fillingDTOs(){

    }

    @Test
    void show() {
        when(peopleService.show(1)).thenReturn(person1);
        assertEquals(person1,peopleService.show(1));

        when(repository.findById(1)).thenReturn(Optional.of(person1));
        Person expectedPerson = repository.findById(1).get();
        assertEquals(person1,expectedPerson);
        verify(repository, times(1)).findById(1);
    }

    @Test
    void showByFullName() {
        when(peopleService.show("Дарин Сергей Владимирович")).thenReturn(Optional.of(person1));
        assertEquals(person1,peopleService.show("Дарин Сергей Владимирович").get());
        verify(peopleService, times(1)).show("Дарин Сергей Владимирович");

        when(repository.findPersonByFullName("Дарин Сергей Владимирович")).thenReturn(Optional.of(person1));
        Person expectedPerson = repository.findPersonByFullName("Дарин Сергей Владимирович").get();
        assertEquals(person1,expectedPerson);
        verify(repository, times(1)).findPersonByFullName("Дарин Сергей Владимирович");
    }

    @Test
    void allPeople() {
        when(modelMapper.map(person1, PersonDTO.class)).thenReturn(personDTO1);
        when(modelMapper.map(person2,PersonDTO.class)).thenReturn(personDTO2);
        List<PersonDTO> personDTOList = new ArrayList<>();
        personDTOList.add(personDTO1);
        personDTOList.add(personDTO2);

        PersonResponse personResponse = new PersonResponse(personDTOList);

        Mockito.when(peopleService.allPeople()).thenReturn(personResponse);
        assertEquals(personResponse, peopleService.allPeople());
    }

    @Test
    void update() {
        Person updatedPerson = new Person();

        updatedPerson.setPersonId(1);
        updatedPerson.setFullName("Дарин Андрей Владимирович");
        updatedPerson.setYearOfBirthday(1986);

        doNothing().when(peopleService).update(1,updatedPerson);
        when(repository.saveAndFlush(updatedPerson)).thenReturn(updatedPerson);

        repository.saveAndFlush(updatedPerson);
        peopleService.update(1,updatedPerson);

        verify(repository, times(1)).saveAndFlush(updatedPerson);
        verify(peopleService, times(1)).update(1,updatedPerson);
    }

    @Test
    void delete() {
        peopleService.delete(1);
        verify(peopleService,times(1)).delete(1);

        repository.deleteById(1);
        verify(repository,times(1)).deleteById(1);
    }

    @Test
    void findPersonByUserName() {
        when(peopleService.findPersonByUserName("user")).thenReturn(Optional.of(person1));
        assertEquals(Optional.of(person1),peopleService.findPersonByUserName("user"));
        verify(peopleService,times(1)).findPersonByUserName("user");
        assertEquals(1,peopleService.findPersonByUserName("user").get().getPersonId());

        when(repository.findPersonByLogin("user")).thenReturn(Optional.of(person1));
        assertEquals(Optional.of(person1),repository.findPersonByLogin("user"));
        verify(repository,times(1)).findPersonByLogin("user");
    }

    @Test
    void convertToPersonFromDTO() {
        when(peopleService.convertToPersonFromDTO(personDTO1)).thenReturn(person1);
        Person actualPerson = peopleService.convertToPersonFromDTO(personDTO1);
        verify(peopleService,times(1)).convertToPersonFromDTO(personDTO1);
        assertEquals(person1,actualPerson);
    }

    @Test
    void convertToDTOFromPerson() {
        when(peopleService.convertToDTOFromPerson(person1)).thenReturn(personDTO1);
        PersonDTO actualPersonDTO = peopleService.convertToDTOFromPerson(person1);
        verify(peopleService, times(1)).convertToDTOFromPerson(person1);
        assertEquals(personDTO1,actualPersonDTO);
    }
}