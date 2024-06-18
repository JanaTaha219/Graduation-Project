package com.example.zh2t.DTOs;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@AllArgsConstructor @NoArgsConstructor
@Data
public class NoteDTO {

    private int id;
    @Lob
    private String url;

    @Lob
    @NotNull(message= "text must not be null")
    @NotEmpty(message= "text must not be null")
    private String text;

    @Lob
    private String userComment;

    @CreatedDate
    private Date creationTime;

   // @JsonManagedReference
    @JsonBackReference
   // @JsonIgnore
    private UserDTOP user;
}
