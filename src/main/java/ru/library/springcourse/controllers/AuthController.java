package ru.library.springcourse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.library.springcourse.dto.PersonDTO;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.securuty.JWTUtil;
import ru.library.springcourse.services.PeopleService;
import ru.library.springcourse.services.RegistrationService;
import ru.library.springcourse.util.ExceptionBuilder;
import ru.library.springcourse.util.LibraryErrorResponse;
import ru.library.springcourse.util.LibraryException;
import ru.library.springcourse.util.PersonValidator;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PersonValidator personValidator;

    private final RegistrationService registrationService;

    private final JWTUtil jwtUtil;

//    private final ModelMapper modelMapper;

    @Autowired
    public AuthController(PersonValidator personValidator, RegistrationService registrationService, JWTUtil jwtUtil, PeopleService peopleService) {
        this.personValidator = personValidator;
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.peopleService = peopleService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("person") Person person) {
        return "auth/registration";
    }

    //TODO: сделать ExceptionHandler для обработки ошибок (вернуть пользователю)
    // сделать везде RestController для работы с библиотекой через Postman
    // провести рефактор кода
    private final PeopleService peopleService;

    @PostMapping("/registration")
    public ResponseEntity performRegistration(@RequestBody @Valid PersonDTO personDTO
            , BindingResult bindingResult) {

        personValidator.validate(peopleService.convertToPersonFromDTO(personDTO), bindingResult);
        ExceptionBuilder.buildErrorMessageForClient(bindingResult);

        registrationService.register(peopleService.convertToPersonFromDTO(personDTO));

        String token = jwtUtil.generateToken(personDTO.getLogin());
        return ResponseEntity.ok(token);

    }

    @ExceptionHandler
    private ResponseEntity<LibraryErrorResponse> measurementHandlerException(LibraryException e) {
        LibraryErrorResponse response = new LibraryErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
