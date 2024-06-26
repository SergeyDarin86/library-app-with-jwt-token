package ru.library.springcourse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.library.springcourse.dto.AuthenticationDTO;
import ru.library.springcourse.dto.PersonDTO;
import ru.library.springcourse.securuty.JWTUtil;
import ru.library.springcourse.securuty.PersonDetails;
import ru.library.springcourse.services.PeopleService;
import ru.library.springcourse.services.RegistrationService;
import ru.library.springcourse.util.ExceptionBuilder;
import ru.library.springcourse.util.LibraryErrorResponse;
import ru.library.springcourse.util.LibraryException;
import ru.library.springcourse.util.PersonValidator;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PersonValidator personValidator;

    private final RegistrationService registrationService;

    private final JWTUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(PersonValidator personValidator, RegistrationService registrationService, JWTUtil jwtUtil, AuthenticationManager authenticationManager, PeopleService peopleService) {
        this.personValidator = personValidator;
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.peopleService = peopleService;
    }

    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getLogin(),
                        authenticationDTO.getPassword());

        try {
            authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            return Map.of("message", "Incorrect credentials");
        }

        String token = jwtUtil.generateToken(authenticationDTO.getLogin());
        return Map.of("jwt-token", token);

    }

    //TODO:
    //TODO: 1) сделать ExceptionHandler для обработки ошибок (вернуть пользователю)
    //TODO: 2) сделать везде RestController для работы с библиотекой через Postman
    //TODO: 3) Сделать отдельные классы PeopleResponse и BookResponse, чтобы не возвращать List в контроллере

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

    @GetMapping("/showUserInfo")
    @ResponseBody
    public String showUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        return personDetails.getUsername();
    }

}
