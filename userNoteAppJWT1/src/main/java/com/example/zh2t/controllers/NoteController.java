package com.example.zh2t.controllers;

import com.example.zh2t.BasicResponce.BasicMessageResponse;
import com.example.zh2t.models.Note;
import com.example.zh2t.models.SecurityUser;
import com.example.zh2t.models.User;
import com.example.zh2t.service.NoteService;
import com.example.zh2t.service.UserService;
import com.example.zh2t.validator.ObjectsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {

    @Autowired
    UserService userService;

    @Autowired
    NoteService noteService;

    @Autowired
    ObjectsValidator<User> userObjectsValidator;
    @Autowired
    ObjectsValidator<Note> noteObjectsValidator;

    @Autowired
    ObjectsValidator validator;

    @GetMapping("")
    public ResponseEntity<?> getAllNotes(@AuthenticationPrincipal SecurityUser securityUser){
        return noteService.getAllNotes(securityUser);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<?> getNoteById(@PathVariable int noteId){
        return noteService.getNoteById(noteId);
    }


    @DeleteMapping("/{noteId}")
    public ResponseEntity<?>deleteNote(@PathVariable int noteId,  @AuthenticationPrincipal SecurityUser securityUser) {
        return noteService.deleteNote(noteId, securityUser);
    }

    @PatchMapping("/{noteId}")
    public ResponseEntity<?> patchNote(@PathVariable int noteId, @RequestBody Map<Object, Object> fields,  @AuthenticationPrincipal SecurityUser securityUser) {
        //return new ResponseEntity<>(securityUser.getUsername(), HttpStatus.OK);
         return noteService.patchNote(noteId, fields, securityUser);
    }

    @GetMapping("/like/{noteId}")
    public ResponseEntity<BasicMessageResponse> getNoteLikes(@PathVariable int noteId){
        return noteService.getNoteLikes(noteId);
    }

    @GetMapping("/numberOfLikes/{noteId}")
    public ResponseEntity<BasicMessageResponse> getNumberOfLikes(@PathVariable int noteId){
        return noteService.getNumberOfLikes(noteId);
    }

    @GetMapping("/numberOfSaving/{noteId}")
    public ResponseEntity<BasicMessageResponse> getNumberOfSavings(@PathVariable int noteId){
        return noteService.getNumberOfSavings(noteId);
    }
}