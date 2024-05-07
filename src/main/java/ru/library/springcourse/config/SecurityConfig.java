package ru.library.springcourse.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.library.springcourse.securuty.AuthProviderImpl;
import ru.library.springcourse.services.PersonDetailsService;

//в этом классе настраивается авторизация и аутентификация для Spring Security
// это главный класс для настроек

// аннотация дает понять Spring, что это конфигурационный класс для Spring Security
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    private final PersonDetailsService personDetailsService;

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    //настраивает аутентификацию
    //даем понять Spring Security, что мы используем провайдер для аутентификации пользователя
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(personDetailsService);
    }

    // даем понять Spring как мы шифруем/ или нет наши пароли
    // без этого бина не запустится программа
    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

}
