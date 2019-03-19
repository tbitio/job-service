package com.easy.jobservice.configuration;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket mailServiceApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(mailServiceApiInfo())
                .useDefaultResponseMessages(false)

                .globalResponseMessage(RequestMethod.GET, getResponseMessages())
                .globalResponseMessage(RequestMethod.POST, getResponseMessages())
                .globalResponseMessage(RequestMethod.PUT, getResponseMessages())
                .globalResponseMessage(RequestMethod.DELETE, getResponseMessages())
                .globalResponseMessage(RequestMethod.PATCH, getResponseMessages())

                .forCodeGeneration(false)
                .select()
                .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
                .paths(PathSelectors.ant("/api/**"))
                .build();
    }

    private ApiInfo mailServiceApiInfo() {
        return new ApiInfoBuilder()
                .title("Documentation for Server REST APIs")
                .version("1.0")
                .build();
    }

    private List<ResponseMessage> getResponseMessages() {
        List<ResponseMessage> responseMessages = new ArrayList<>();

        responseMessages.add(
                new ResponseMessageBuilder()
                        .code(500)
                        .message("Failure. Unexpected condition was encountered.")
                        .build()
        );

        return responseMessages;
    }
}
