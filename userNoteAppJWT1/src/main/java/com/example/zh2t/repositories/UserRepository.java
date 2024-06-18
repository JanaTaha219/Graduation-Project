package com.example.zh2t.repositories;

import com.example.zh2t.models.Note;
import com.example.zh2t.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    public User getUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.uniqueName) LIKE LOWER(CONCAT('%', :uniqueName, '%'))")
    List<User> findUsersWithUniqueNameLike(@Param("uniqueName") String uniqueName);
}
