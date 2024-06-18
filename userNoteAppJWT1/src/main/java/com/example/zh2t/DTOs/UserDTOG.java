package com.example.zh2t.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.Set;

@NoArgsConstructor
@Data
public class UserDTOG {

    private String uniqueName;

    private String email;

    private String bio;

    private Date birthDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonManagedReference
    private Set<NoteDTO> notes;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonManagedReference
    private Set<UserDTOP> followers;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonManagedReference
    private Set<UserDTOP> following;

    public UserDTOG(String uniqueName, String email, String bio, Date birthDate, Set<NoteDTO> notes, Set<UserDTOP> followers, Set<UserDTOP> following) {
        this.uniqueName = uniqueName;
        this.email = email;
        this.bio = bio;
        this.birthDate = birthDate;
        this.notes = notes;
        this.followers = followers;
        this.following = following;
    }
    public UserDTOG(String uniqueName, String email, String bio, Date birthDate){
        this.uniqueName = uniqueName;
        this.email = email;
        this.bio = bio;
        this.birthDate = birthDate;
    }
}
