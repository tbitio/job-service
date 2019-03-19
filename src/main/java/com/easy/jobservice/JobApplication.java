package com.easy.jobservice;

import com.easy.jobservice.configuration.*;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableAutoConfiguration(exclude= {
        EmbeddedMongoAutoConfiguration.class
})
@EnableMongoRepositories(basePackages = {"com.easy.jobservice.repository"})
@Import(
        value = {
                SwaggerConfiguration.class,
                ResourceServerConfiguration.class,
                GlobalMethodSecurityConfiguration.class,
                JwtConfiguration.class,
                ResourceServerConfiguration.class
        })
@ComponentScan({ "com.easy.jobservice" })
public class JobApplication {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }
}
