package com.example.zh2t.service;

import com.example.zh2t.BasicResponce.BasicMessageResponse;
import com.example.zh2t.DTOs.NoteDTO;
import com.example.zh2t.DTOs.NoteDto2;
import com.example.zh2t.DTOs.UserDTOP;
import com.example.zh2t.Mappers.EntityDtoConverter;
import com.example.zh2t.exceptions.ObjectNotFoundException;
import com.example.zh2t.models.Note;
import com.example.zh2t.models.SecurityUser;
import com.example.zh2t.repositories.NoteRepository;
import com.example.zh2t.validator.ObjectsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepo;
    @Autowired

    ObjectsValidator validator = new ObjectsValidator();

    @Autowired
    EntityDtoConverter converter;



    public void addNote(Note note){ noteRepo.save(note);}

    public ResponseEntity<?> getAllNotes(SecurityUser securityUser){
        if(!securityUser.getRole().equals("ROLE_ADMIN")) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        List<NoteDto2> notes = noteRepo.findAll().stream()
                .map(note -> {
                    NoteDto2 noteDto = converter.convertToDto(note, NoteDto2.class);
                    noteDto.setUniqueName(note.getUser().getUniqueName());
                    return noteDto;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>( notes, HttpStatus.OK);
    }

    public Optional<Note> getNoteBId(int id){

        return noteRepo.findById(id);
    }

    public ResponseEntity<?> getNoteById(int id) {
        Optional<Note>note =  getNoteBId(id);
        if(note.isPresent()){
            note.get().getUser().setPassword(null);
            return new ResponseEntity<>(converter.convertToDto(note.get(), NoteDTO.class), HttpStatus.OK);
        }
        return  new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Query("select u from Note u where u.user.uniqueName = ?1")
    public List<Note> getNotesByUserId(String userId) {
        return noteRepo.findByUserUniqueName(userId);
    }

    public ResponseEntity<?> deleteNote(int noteId, SecurityUser securityUser){
        Optional<Note> note = getNoteBId(noteId);
        if(note.isPresent()) {
            if(!securityUser.getUsername().equals(note.get().getUser().getUniqueName()) && !securityUser.getRole().equals("ROLE_ADMIN"))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            noteRepo.delete(note.get());
            return new ResponseEntity<>("deleted!", HttpStatus.OK);
        }
        return  new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public void deleteNote(Note note){

         noteRepo.delete(note);
    }

    public ResponseEntity<?> patchNote(int noteId,  Map<Object, Object> fields, SecurityUser securityUser) {
        Optional<Note> note = getNoteBId(noteId);
        if(note.isPresent()){
            if(!securityUser.getUsername().equals(note.get().getUser().getUniqueName()) && !securityUser.getRole().equals("ROLE_ADMIN"))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            fields.forEach((key, value)->{
                Field field = ReflectionUtils.findField(Note.class, (String)key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, note.get(), value);
            });
            validator.validate(note.get());
            addNote(note.get());
            return new ResponseEntity<>("Updated!", HttpStatus.OK);
        }
        return  new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<BasicMessageResponse> getNoteLikes(int noteId){
        Note note = getNoteBId(noteId).orElseThrow(() -> new ObjectNotFoundException("Note is not in the database"));
        Set<UserDTOP> userDTOPS = note.getLikingUsers().stream()
                .map(user -> {
                    user.setPassword(null);
                    return converter.convertToDto(user, UserDTOP.class);
                })
                .collect(Collectors.toSet());
        return new ResponseEntity<>(new BasicMessageResponse("Message", userDTOPS, 200), HttpStatus.OK);
    }

    public ResponseEntity<BasicMessageResponse> getNumberOfLikes(int noteId){
        Note note = getNoteBId(noteId).orElseThrow(() -> new ObjectNotFoundException("Note is not in the database"));
        return new ResponseEntity<>(new BasicMessageResponse("number of likes:",note.getLikingUsers().size(),200), HttpStatus.OK);
    }
    public ResponseEntity<BasicMessageResponse> getNumberOfSavings(int noteId){
        Note note = getNoteBId(noteId).orElseThrow(() -> new ObjectNotFoundException("Note is not in the database"));
        return new ResponseEntity<>(new BasicMessageResponse("number of savings:",note.getSavingUsers().size(),200), HttpStatus.OK);
    }



}
