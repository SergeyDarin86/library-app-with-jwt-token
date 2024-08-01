package ru.library.springcourse;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@SpringBootApplication
public class LibraryAppWithSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryAppWithSecurityApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("ru.library.springcourse.controllers"))
				.paths(PathSelectors.any())
				.build()
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
