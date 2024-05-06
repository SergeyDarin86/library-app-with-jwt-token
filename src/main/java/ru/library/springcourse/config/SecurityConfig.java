package ru.library.springcourse.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import ru.library.springcourse.securuty.AuthProviderImpl;

//в этом классе настраивается авторизация и аутентификация для Spring Security
// это главный класс для настроек

// аннотация дает понять Spring, что это конфигурационный класс для Spring Security
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    private final AuthProviderImpl authProvider;

    @Autowired
    public SecurityConfig(AuthProviderImpl authProvider) {
        this.authProvider = authProvider;
    }

    //настраивает аутентификацию
    //даем понять Spring Security, что мы используем провайдер для аутентификации пользователя
    protected void configure(AuthenticationManagerBuilder auth){
        auth.authenticationProvider(authProvider);
    }

}
