package com.example.zh2t.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class User {
   @Id
   @NotNull(message= "unique name must not be null")
   @NotEmpty(message= "unique name must not be null")
   private String uniqueName;

   @NotNull(message= "Email must not be null")
   @NotEmpty(message= "Email must not be null")
   @Email(message= "Email must have email format")
   private String email;

   @Size(min = 8, message= "Password must be at least 8 chars")
   @NotNull(message= "Password must not be null")
   @NotEmpty(message= "Password must not be null")
   private String password;

   private String bio;

   @Past(message= "Birth date must be in past")
   private Date birthDate;

   @JsonManagedReference
   @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
   private Set<Note> notes = new HashSet<>();

   @ManyToMany(mappedBy = "following")
   @JsonManagedReference
   private Set<User> followers = new HashSet<>();

   @ManyToMany
   @JoinTable(name = "followers",
           joinColumns = @JoinColumn(name = "follower_id"),
           inverseJoinColumns = @JoinColumn(name = "following_id"))
  // @JsonBackReference
   private Set<User> following = new HashSet<>();

   //@Enumerated(value = EnumType.STRING)
   private String role;

   @ManyToMany
   @JoinTable(name = "likes",
           joinColumns = @JoinColumn(name = "user_id"),
           inverseJoinColumns = @JoinColumn(name = "note_id"))
   private Set<Note> likedNotes = new HashSet<>();

   @ManyToMany
   @JoinTable(name = "saves",
           joinColumns = @JoinColumn(name = "user_id"),
           inverseJoinColumns = @JoinColumn(name = "note_id"))
   private Set<Note> savedNotes = new HashSet<>();


   public User(String uniqueName, String email, String bio, String password, Date birthDate) {
      this.uniqueName = uniqueName;
      this.email = email;
      this.password = password;
      this.bio = bio;
      this.birthDate = birthDate;
   }

   public User(String uniqueName, String email, String bio, Date birthDate) {
      this.uniqueName = uniqueName;
      this.email = email;
      this.bio = bio;
      this.birthDate = birthDate;
   }
   public User(String name){
      this.uniqueName = name;
   }


}