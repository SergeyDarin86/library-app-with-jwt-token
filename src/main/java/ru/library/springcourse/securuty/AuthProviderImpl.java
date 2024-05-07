package ru.library.springcourse.securuty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.library.springcourse.services.PersonDetailsService;

import java.util.Collections;

// проверяем вводимые логин и пароль на соответствие

//@Component
public class AuthProviderImpl implements AuthenticationProvider {

    private final PersonDetailsService personDetailsService;

    @Autowired
    public AuthProviderImpl(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    // здесь будет лежать наша логика аутентификации
    // этот метод вызывается самим Security
    // данные с формы будут переданы в этот метод
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String userName = authentication.getName();

        UserDetails personDetails = personDetailsService.loadUserByUsername(userName);

        String password = authentication.getCredentials().toString();   // получаем пароль

        if (!password.equals(personDetails.getPassword()))
            throw new BadCredentialsException("Неверный пароль");
        // этот класс реализует Authentication, поэтому мы можем его вернуть из этого метода
        // передаем principal(пользователь), credential(пароль) и список прав (пока пустой)
        // объект, который будет возвращен здесь будет помещен в сессию и доставаться каждый раз,
        // когда мы будем обращаться к серверу с запросом
        return new UsernamePasswordAuthenticationToken(personDetails,password, Collections.emptyList());
    }

    //это чисто технический метод. Нужен Spring, чтобы понять для какого объекта работает
    // AuthenticationProvider (пока у нас он только один) и мы можем всегда возвращать true
    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
