package ru.library.springcourse.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.shaded.com.google.common.collect.Lists;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.List;

/**
 * Swagger-конфигурация для проекта
 *
 * @author Sergey D.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Заголовок авторизации
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Название тега для Сервиса Аутентификации
     */
    public static final String TAG_AUTH_CONTROLLER = "Api сервиса Аутентификации";

    /**
     * Название тега для Сервиса Библиотеки
     */
    public static final String TAG_LIBRARY = "Api сервиса библиотеки";

    /**
     * Описание сервиса библиотеки
     */
    public static final String DESCRIPTION_LIBRARY = "Сервис предназначен для:" +
            " \n- редактирования и удаления данных о читателях;" +
            " \n- регистрации новых книг, редактировании и удалении данных об устаревших книгах; " +
            " \n- назначения книг читателям и освобождения книг.";

    /**
     * Описание сервиса аутентификации
     */
    public static final String DESCRIPTION_AUTH_CONTROLLER = "Сервис предназначен для регистрации читателей";

    /**
     * Бин для настройки документации Swagger
     *
     * @return Docket docket
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("ru.library.springcourse.controllers"))
                .paths(PathSelectors.any())
                .build()
                .tags(new Tag(TAG_AUTH_CONTROLLER, DESCRIPTION_AUTH_CONTROLLER))
                .tags(new Tag(TAG_LIBRARY, DESCRIPTION_LIBRARY))
                .securityContexts(Lists.newArrayList(securityContext()))
                .securitySchemes(Lists.newArrayList(apiKey()))
                .apiInfo(apiInfo()).useDefaultResponseMessages(false);
    }

    /**
     * Создание ключа Api
     * @return ApiKey
     */
    private ApiKey apiKey() {
        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
    }

    /**
     * Настройка контекста security
     * @return SecurityContext
     */
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    /**
     * Настройка аутентификации по умолчанию
     *
     * @return List<SecurityReference> list
     */
    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(
                new SecurityReference("JWT", authorizationScopes));
    }

    /**
     * Настройка информации о проекте
     * @return ApiInfo
     */
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "My Library REST API",
                "Сервис для работы с библиотекой",
                "1.0.0",
                "Условия обслуживания",
                new Contact("Дарин Сергей", "https://securetoken.google.com", "www.swd86@mail.com"),
                "Лицензия API", "www.my-license-url.com", Collections.emptyList());
    }

}
