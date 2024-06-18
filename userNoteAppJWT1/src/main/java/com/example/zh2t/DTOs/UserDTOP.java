package com.example.zh2t.DTOs;

import com.example.zh2t.models.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTOP {
        @NotNull
        @NotEmpty
        private String uniqueName;

        @NotNull
        @NotEmpty
        @Email
        private String email;

        @Nullable
        private String bio;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String password;

        @Past
        private Date birthDate;

        private String role;

        public UserDTOP(String uniqueName, String email, String bio, Date birthDate) {
                this.uniqueName = uniqueName;
                this.email = email;
                this.bio = bio;
                this.birthDate = birthDate;
        }

        public UserDTOP(String uniqueName, String password){
                this.uniqueName = uniqueName;
                this.password = password;
        }


}
