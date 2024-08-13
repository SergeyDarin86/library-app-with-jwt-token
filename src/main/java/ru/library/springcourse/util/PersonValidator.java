package ru.library.springcourse.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.services.PeopleService;

/**
 * Класс, который предназначен для проверки данных об экземпляре читателя на корректность
 *
 * @author Sergey D.
 */
@Slf4j
@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    //В логике данного приложения не может быть двух пользователей
    // с одинаковыми ФИО и логином (именем_пользователя)

    /**
     * Метод для валидации входящих данных
     *
     * @param target Экземпляр читателя
     * @param errors Ошибка, которая будет выдана пользователю в случае некорректных данных
     */

    @Override
    public void validate(Object target, Errors errors) {
        log.info("Start method validate(target, errors) for PersonValidator, target is: {}", target);
        Person person = (Person) target;

        if (peopleService.show(person.getFullName()).isPresent()) {
            if (peopleService.show(person.getFullName()).get().getPersonId() != person.getPersonId()) {
                errors.rejectValue("fullName", "", "Человек с таким именем уже существует");
            }
        }

        if (peopleService.findPersonByUserName(person.getLogin()).isPresent()) {
            if (peopleService.findPersonByUserName(person.getLogin()).get().getPersonId() != person.getPersonId()) {
                errors.rejectValue("login", "", "Такой пользователь уже существует");
            }
        }

    }

}
