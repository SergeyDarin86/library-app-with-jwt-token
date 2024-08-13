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

/**
 * Сервис для работы с читателями в библиотеке (редактирование, удаление, поиск, отображение списка всех читателей)
 *
 * @author Sergey D.
 */
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

    /**
     * Метод для поиска всех читателей в библиотеке
     *
     * @return Список читателей
     */
    public PersonResponse allPeople() {
        return new PersonResponse(peopleRepository.findAll().stream().map(this::convertToDTOFromPerson).toList());
    }

    /**
     * Метод для получения экземпляра читателя по его идентификационному номеру
     *
     * @param personId Идентификационный номер читателя
     * @return Экземпляр Читателя
     */

    public Person show(int personId) {
        log.info("Start method show(id) for peopleService, id is: {}", personId);

        // получаем данные из контекста (из потока)
        // для каждого пользователя будет создан свой поток
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        System.out.println(personDetails.getPerson());

        return peopleRepository.findById(personId).orElse(null);
    }

    /**
     * Метод для получения экземпляра читателя по его ФИО (полное совпадение)
     *
     * @param fullName ФИО читателя
     * @return Экземпляр Читателя либо NULL
     */

    public Optional<Person> show(String fullName) {
        log.info("Start method show(fullName) for peopleService, fullName is: {}", fullName);
        return peopleRepository.findPersonByFullName(fullName);
    }

    /**
     * Метод для обновления экземпляра читателя
     *
     * @param id            Идентификационный номер читателя
     * @param updatedPerson Редактируемый читатель
     * @return DTO для сущности Читатель
     */
    @Transactional
    public PersonDTO update(int id, Person updatedPerson) {
        log.info("Start method update(personId, Person) for peopleService, personId is: {}", id);
        peopleRepository.saveAndFlush(updatedPerson);
        return convertToDTOFromPerson(updatedPerson);
    }

    // еще один новый вариант метода - НЕ ПРОТЕСТИРОВАН

    /**
     * Метод для получения экземпляра читателя
     *
     * @param id        Идентификационный номер читателя
     * @param personDTO Объект DTO для читателя
     * @return Экземпляр читателя
     */
    @Transactional
    public Person getConvertedPerson(int id, PersonDTO personDTO) {
        log.info("Start method getConvertedPerson(personId, PersonDTO) for peopleService, personId is: {}", id);

        Person convertedPerson = convertToPersonFromDTO(personDTO);
        convertedPerson.setPersonId(id);
        convertedPerson.setRole(show(id).getRole());
        convertedPerson.setPassword(passwordEncoder.encode(personDTO.getPassword()));

        return convertedPerson;
    }

    /**
     * Метод для удаления экземпляра читателя
     *
     * @param id Идентификационный номер читателя
     */

    @Transactional
    public void delete(int id) {
        log.info("Start method delete(id) for peopleService, id is: {}", id);
        peopleRepository.deleteById(id);
    }

    /**
     * Метод для получения экземпляра читателя по его логину
     *
     * @param login Логин читателя
     * @return Экземпляр читателя либо NULL
     */
    public Optional<Person> findPersonByUserName(String login) {
        log.info("Start method findPersonByUserName(login) for peopleService, login is: {}", login);
        return peopleRepository.findPersonByLogin(login);
    }

    /**
     * Метод для преобразования PersonDTO в экземпляр читателя
     *
     * @param personDTO Объект DTO для читателя
     * @return Экземпляр читателя
     */
    public Person convertToPersonFromDTO(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    /**
     * Метод для преобразования экземпляра читателя в объект DTO
     *
     * @param person Экземпляр читателя
     * @return Объект DTO для читателя
     */
    public PersonDTO convertToDTOFromPerson(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }

}
