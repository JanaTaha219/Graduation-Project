package com.example.zh2t.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
public class MailStructure {
    private String subject;
    private String message;
    private String email;

    public MailStructure(String email){
        this.email = email;
    }
}
