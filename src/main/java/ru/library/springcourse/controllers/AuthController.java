package ru.library.springcourse.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
import ru.library.springcourse.resource.AuthControllerResource;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController implements AuthControllerResource {

    PersonValidator personValidator;

    RegistrationService registrationService;

    JWTUtil jwtUtil;

    AuthenticationManager authenticationManager;

    PeopleService peopleService;

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
