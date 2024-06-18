package com.example.zh2t.service;

import com.example.zh2t.BasicResponce.BasicMessageResponse;
import com.example.zh2t.models.Note;
import com.example.zh2t.models.SecurityUser;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NoteService {
    public ResponseEntity<?> getAllNotes(SecurityUser securityUser);
    public void addNote(Note note);
    public Optional<Note> getNoteBId(int id);
    public ResponseEntity<?> patchNote(int noteId,  Map<Object, Object> fields, SecurityUser securityUser);
    public ResponseEntity<?> getNoteById(int id);
    public List<Note> getNotesByUserId(String userId);
    public ResponseEntity<?> deleteNote(int noteId,  SecurityUser securityUser);
    public void deleteNote(Note note);

    public ResponseEntity<BasicMessageResponse> getNoteLikes(int noteId);
    public ResponseEntity<BasicMessageResponse> getNumberOfLikes(int noteId);
    public ResponseEntity<BasicMessageResponse> getNumberOfSavings(int noteId);
}
