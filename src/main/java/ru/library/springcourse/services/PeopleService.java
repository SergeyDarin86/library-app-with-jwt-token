package ru.library.springcourse.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.PeopleRepository;

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

}
