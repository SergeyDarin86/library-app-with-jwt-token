package ru.library.springcourse.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.repositories.PeopleRepository;

@Slf4j
@Service
public class RegistrationService {

    private final PeopleRepository peopleRepository;

    public RegistrationService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    @Transactional
    public void register(Person person) {
        log.info("Start method register(person) for RegistrationService, person is: {}", person);
        peopleRepository.save(person);
    }

}
