package com.example.zh2t;

import com.example.zh2t.DTOs.NoteDTO;
import com.example.zh2t.DTOs.UserDTOG;
import com.example.zh2t.models.Note;
import com.example.zh2t.models.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Date;
import java.util.Set;

@SpringBootApplication
@EnableJpaAuditing
public class Zh2tApplication {

    public static void main(String[] args) {
        SpringApplication.run(Zh2tApplication.class, args);
        System.out.println(new Date(System.currentTimeMillis()));
    }

}
