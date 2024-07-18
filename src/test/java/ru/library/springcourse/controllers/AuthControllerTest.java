package ru.library.springcourse.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import ru.library.springcourse.dto.AuthenticationDTO;
import ru.library.springcourse.dto.PersonDTO;
import ru.library.springcourse.models.Person;
import ru.library.springcourse.securuty.JWTUtil;
import ru.library.springcourse.securuty.PersonDetails;
import ru.library.springcourse.services.PeopleService;
import ru.library.springcourse.services.RegistrationService;
import ru.library.springcourse.util.LibraryException;
import ru.library.springcourse.util.PersonValidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthControllerTest {

    @Mock
    PersonValidator personValidator;

    @Mock
    RegistrationService registrationService;

    @Mock
    JWTUtil jwtUtil;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    PeopleService peopleService;

    @Mock
    Authentication authentication;

    @Mock
    PersonDetails personDetails;

    @Mock
    BindingResult bindingResult;

    @InjectMocks
    AuthController authController;

    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    // TODO: описать решение проблемы с SecurityContextHolder и Authentication
    // TODO: как обойти аутентификацию

    @BeforeEach
    public void initSecurityContext() {
        when(authentication.getPrincipal()).thenReturn("mockedPassword");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(personDetails);

    }

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void performLogin() throws Exception {

        AuthenticationDTO authenticationDTO = new AuthenticationDTO();
        authenticationDTO.setLogin("user");
        authenticationDTO.setPassword("user");

        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getLogin(),
                        authenticationDTO.getPassword());

        authenticationManager.authenticate(authInputToken);
        String token = "token";
        when(jwtUtil.generateToken(authenticationDTO.getLogin())).thenReturn(token);

        authController.performLogin(authenticationDTO);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(authenticationDTO))
                )
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    void performLoginWithThrowBadCredentialException() throws Exception {

        AuthenticationDTO authenticationDTO = new AuthenticationDTO();
        authenticationDTO.setLogin("user7");
        authenticationDTO.setPassword("user7");

        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getLogin(),
                        authenticationDTO.getPassword());

        when(authenticationManager.authenticate(authInputToken)).thenThrow(BadCredentialsException.class);

        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(authenticationDTO))
                )
                .andExpect(status().is2xxSuccessful());
        MvcResult mvcResult = resultActions.andReturn();
        String actualBody = mvcResult.getResponse().getContentAsString();

        assertEquals("{\"message\":\"Incorrect credentials\"}", actualBody);
    }

    @Test
    void performRegistration() throws Exception {

        PersonDTO personDTO = new PersonDTO();
        personDTO.setLogin("user6");
        personDTO.setPassword("user6");
        personDTO.setYearOfBirthday(1999);
        personDTO.setFullName("Тестовый Сергей Владимирович");

        Person person = new Person();
        person.setLogin("user6");
        person.setPassword("user6");

        when(peopleService.convertToPersonFromDTO(personDTO)).thenReturn(person);

        doNothing().when(personValidator).validate(person, bindingResult);
        doNothing().when(registrationService).register(person);

        authController.performRegistration(personDTO, bindingResult);

        String token = "token";
        when(jwtUtil.generateToken(personDTO.getLogin())).thenReturn(token);

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(personDTO))
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    void performRegistrationWithLibraryException() throws Exception {

        PersonDTO personDTO = new PersonDTO();
        personDTO.setLogin("user");
        personDTO.setPassword("user");
        personDTO.setYearOfBirthday(1899);
        personDTO.setFullName("Дарин Сергей Владимирович");

        String errorMsg = "Год рождения должен быть больше 1900г.";

        when(authController.performRegistration(personDTO, bindingResult)).thenThrow(new LibraryException(errorMsg));

        ResultActions resultActions = mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(personDTO))
                )
                .andExpect(status().is4xxClientError());

        MvcResult mvcResult = resultActions.andReturn();
        assertNotNull(mvcResult.getResponse().getContentAsString());

    }

    @Test
    void showUserInfo() throws Exception {
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        when(authentication.getPrincipal()).thenReturn(personDetails);
        when(personDetails.getUsername()).thenReturn("user");

        SecurityContextHolder.getContext().getAuthentication().getCredentials();

        ResultActions resultActions = mockMvc.perform(get("/auth/showUserInfo"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        MvcResult mvcResult = resultActions.andReturn();
        String actualUserName = mvcResult.getResponse().getContentAsString();

        assertEquals("user", actualUserName);
    }
}