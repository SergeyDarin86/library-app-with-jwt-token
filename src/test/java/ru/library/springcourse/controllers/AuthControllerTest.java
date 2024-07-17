package ru.library.springcourse.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.library.springcourse.dto.AuthenticationDTO;
import ru.library.springcourse.securuty.JWTUtil;
import ru.library.springcourse.services.PeopleService;
import ru.library.springcourse.services.RegistrationService;
import ru.library.springcourse.util.PersonValidator;

import static org.mockito.Mockito.when;
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

    @InjectMocks
    AuthController authController;

    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
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
    void performRegistration() {
    }

    @Test
    void showUserInfo() {
    }
}