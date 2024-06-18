package com.example.zh2t.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "uniqueName")
    @JsonManagedReference
    private User user;

    @ManyToMany(mappedBy = "likedNotes")
    @JsonManagedReference
    private Set<User> likingUsers = new HashSet<>();

    @ManyToMany(mappedBy = "savedNotes")
    @JsonManagedReference
    private Set<User> savingUsers = new HashSet<>();



    public Note(String url, String text, String userComment, Date creationTime, User user) {
        this.url = url;
        this.text = text;
        this.userComment = userComment;
        this.creationTime = creationTime;
        this.user = user;
    }
}