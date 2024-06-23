package com.example.zh2t;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Date;

@SpringBootApplication
@EnableJpaAuditing
public class Zh2tApplication {

    public static void main(String[] args) {
        SpringApplication.run(Zh2tApplication.class, args);
        System.out.println(new Date(System.currentTimeMillis()));
    }

}
