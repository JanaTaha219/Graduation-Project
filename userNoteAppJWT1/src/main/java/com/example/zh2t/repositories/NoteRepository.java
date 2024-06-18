package com.example.zh2t.repositories;

import com.example.zh2t.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {
    List<Note> findByUserUniqueName(String userId);

    @Query(value = "SELECT * FROM likes WHERE l.user_id = :userName", nativeQuery = true)
    public List<Note> getLikedNotesByUniqueName(String userName);
}
