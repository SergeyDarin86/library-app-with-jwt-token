package ru.library.springcourse.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.library.springcourse.dto.PersonDTO;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.PeopleRepository;
import ru.library.springcourse.securuty.PersonDetails;
import ru.library.springcourse.util.PersonResponse;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public PersonResponse allPeople() {
        return new PersonResponse(peopleRepository.findAll().stream().map(this::convertToDTOFromPerson).toList());
    }

    public Person show(int personId) {
        log.info("Start method show(id) for peopleService, id is: {}", personId);

        // получаем данные из контекста (из потока)
        // для каждого пользователя будет создан свой поток
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        System.out.println(personDetails.getPerson());

        return peopleRepository.findById(personId).orElse(null);
    }

    public Optional<Person> show(String fullName) {
        log.info("Start method show(fullName) for peopleService, fullName is: {}", fullName);
        return peopleRepository.findPersonByFullName(fullName);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        log.info("Start method update(personId, Person) for peopleService, personId is: {}", id);
        updatedPerson.setPersonId(id);
        updatedPerson.setPassword(passwordEncoder.encode(updatedPerson.getPassword()));
        peopleRepository.saveAndFlush(updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        log.info("Start method delete(id) for peopleService, id is: {}", id);
        peopleRepository.deleteById(id);
    }

    public Optional<Person> findPersonByUserName(String login) {
        log.info("Start method findPersonByUserName(login) for peopleService, login is: {}", login);
        return peopleRepository.findPersonByLogin(login);
    }

    public Person convertToPersonFromDTO(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    public PersonDTO convertToDTOFromPerson(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }

}
