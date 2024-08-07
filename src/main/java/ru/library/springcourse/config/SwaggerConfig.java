package ru.library.springcourse.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    public static final String TAG_AUTH_CONTROLLER = "Api сервиса Аутентификации";
    public static final String TAG_LIBRARY = "Api сервиса библиотеки";

    public static final String DESCRIPTION_LIBRARY = "Сервис предназначен для:" +
                " \n- редактирования и удаления данных о читателях;" +
                " \n- регистрации новых книг, редактировании и удалении данных об устаревших книгах; " +
                " \n- назначения книг читателям и освобождения книг.";

    public static final String DESCRIPTION_AUTH_CONTROLLER = "Сервис предназначен для регистрации читателей";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("ru.library.springcourse.controllers"))
                .paths(PathSelectors.any())
                .build()
                .tags(new Tag(TAG_AUTH_CONTROLLER, DESCRIPTION_AUTH_CONTROLLER))
                .tags(new Tag(TAG_LIBRARY, DESCRIPTION_LIBRARY))
                .apiInfo(apiInfo()).useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "My Library REST API",
                "Сервис для работы с библиотекой",
                "1.0.0",
                "Условия обслуживания",
                new Contact("Дарин Сергей", "https://securetoken.google.com", "www.swd86@mail.com"),
                "License of API", "www.my-license-url.com", Collections.emptyList());
    }
}
