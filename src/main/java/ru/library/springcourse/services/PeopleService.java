package ru.library.springcourse.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.PeopleRepository;
import ru.library.springcourse.securuty.PersonDetails;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> allPeople(){
        return peopleRepository.findAll();
    }

    public Person show(int personId){
        log.info("Start method show(id) for peopleService, id is: {}", personId);

        // получаем данные из контекста (из потока)
        // для каждого пользователя будет создан свой поток
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        System.out.println(personDetails.getPerson());

        return peopleRepository.findById(personId).orElse(null);
    }

    public Optional<Person> show(String fullName){
        log.info("Start method show(fullName) for peopleService, fullName is: {}", fullName);
        return peopleRepository.findPersonByFullName(fullName);
    }

    @Transactional
    public void save (Person person){
        log.info("Start method save(person) for peopleService, person is: {}", person);
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson){
        log.info("Start method update(personId, Person) for peopleService, personId is: {}", id);
        updatedPerson.setPersonId(id);
        peopleRepository.saveAndFlush(updatedPerson);
    }

    @Transactional
    public void delete(int id){
        log.info("Start method delete(id) for peopleService, id is: {}", id);
        peopleRepository.deleteById(id);
    }

    public Optional<Person>findPersonByBookId(Integer bookId){
        log.info("Start method findPersonByBookId(bookId) for peopleService, bookId is: {}", bookId);
        return peopleRepository.findPersonByBookId(bookId);
    }

    public Optional<Person> findPersonByUserName(String login){
        return peopleRepository.findPersonByLogin(login);
    }

}
