package com.tutorial.crud.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.AuthorizationScopeBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    //Swagger es para documentar y hacer pruebas similares a las de postman. Se prueba con el proyecto corriendo en localhost:8080/swagger-ui/index.html

    @Bean
    public Docket api () {
        return  new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .securityContexts(Arrays.asList(securityContext()))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                ;
    }

    private ApiKey apiKey (){
        return new ApiKey("JWT","Authorization","header");
    }

    private SecurityContext securityContext () {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth(){
        AuthorizationScope authorizationScope = new AuthorizationScope("global","accessEvent");
        AuthorizationScope [] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }

    private ApiInfo  apiInfo(){
        return new ApiInfo(
                "Tutorial Jwt",
                "Descripcion",
                "2.0",
                "Terminos y Condiciones",
                new Contact("Luigi Code", "www.luigicode.es","micorreo@gmail.com"),
                "Licencia",
                "www.licencia.com",
                Collections.emptyList());

    }

}
