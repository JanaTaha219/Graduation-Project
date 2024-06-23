package com.example.zh2t.controllers;

import com.example.zh2t.BasicResponce.BasicMessageResponse;
import com.example.zh2t.DTOs.NoteDTO;
import com.example.zh2t.DTOs.NoteDto2;
import com.example.zh2t.DTOs.UserDTOP;
import com.example.zh2t.Mappers.EntityDtoConverter;
import com.example.zh2t.models.*;
import com.example.zh2t.service.NoteService;
import com.example.zh2t.service.UserService;
import com.example.zh2t.validator.ObjectsValidator;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/users")
public class UserController {

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

    @Autowired
    EntityDtoConverter entityDtoConverter;


    @GetMapping("/likedBy/{uniquename}")
    public List<NoteDto2> notesLikedByUser(@PathVariable String uniquename){
        return userService.notesLikedByUser(uniquename);
    }
    @GetMapping("/out")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication, @AuthenticationPrincipal SecurityUser securityUser) {
        return userService.logout(request, response, authentication, securityUser);
    }
    @GetMapping("/hi")
    public String sayHi(){
        return "Hi!";
    }

    @GetMapping("/currentUser")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal SecurityUser securityUser){
        if (securityUser != null) {
            Map x = new HashMap();
            x.put("userName", securityUser.getUsername());
            x.put("role", securityUser.getRole());
            return new ResponseEntity<>(new BasicMessageResponse(x, 200), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("null", HttpStatus.OK);
        }
    }

    @GetMapping("/password")
    public ResponseEntity<?> getCurrentUserPassword(@AuthenticationPrincipal SecurityUser securityUser){
        if (securityUser != null) {
            Map x = new HashMap();
            x.put("password", securityUser.getPassword());
            return new ResponseEntity<>(new BasicMessageResponse(x, 200), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("null", HttpStatus.OK);
        }
    }

    @GetMapping("/{uniquename}/likes/{noteId}")
    public boolean doesUserLikeNote(@PathVariable String uniquename,@PathVariable int noteId){
        return userService.doesUserLikeNote(uniquename, noteId);
    }

    @GetMapping("/{uniquename}/saves/{noteId}")
    public boolean doesUserSaveNote(@PathVariable String uniquename,@PathVariable int noteId){
        return userService.doesUserSaveNote(uniquename, noteId);
    }

    @GetMapping("/like/{uniqueName}")
    public List<UserDTOP> findUsersWithUniqueNameLike(@PathVariable String uniqueName){
        return  userService.findUsersWithUniqueNameLike(uniqueName);
    }

    @PostMapping("")
    public ResponseEntity<?> addUser(@RequestBody UserDTOP user) {
        return userService.addUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserByIdR(@PathVariable String id) {
        return userService.getUserByIdR(id);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers(@AuthenticationPrincipal SecurityUser securityUser) {
        return userService.getAllUsers(securityUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable String id, @AuthenticationPrincipal SecurityUser securityUser){
        return userService.deleteUser(id, securityUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchUser(@PathVariable String id, @RequestBody Map<Object, Object> fields, @AuthenticationPrincipal SecurityUser securityUser){
        return userService.patchUser(id, fields, securityUser);
    }

    //A follows B
    @PostMapping("/{idA}/follow/{idB}")
    public ResponseEntity<?> follow(@PathVariable String idA, @PathVariable String idB, @AuthenticationPrincipal SecurityUser securityUser){
        return userService.followUser(idA, idB, securityUser);
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable String id){
        return userService.getFollowers(id);
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<?> getFollowing(@PathVariable String id){
        return userService.getFollowing(id);
    }

    //A unfollows B
    @DeleteMapping ("/{idA}/unfollow/{idB}")
    public ResponseEntity<?> unfollow(@PathVariable String idA, @PathVariable String idB, @AuthenticationPrincipal SecurityUser securityUser){
        return userService.unfollowUser(idA, idB, securityUser);
    }

    //add note to a user
    @PostMapping(value ="/{userId}/note", consumes = "application/json")
    public ResponseEntity<?> addNoteToUser(@PathVariable String userId,@RequestBody NoteDTO note, @AuthenticationPrincipal SecurityUser securityUser) {
        return userService.addNoteToUser(userId, entityDtoConverter.convertToEntity(note, Note.class), securityUser);
    }

    //get user's notes
    @GetMapping("/{userId}/notes")
    public ResponseEntity<?> getAllUserNotes(@PathVariable String userId){
            return userService.getAllUserNotes(userId);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody UserDTOP user){
        return userService.authenticate(user);
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<?> forgetPassword(@RequestBody MailStructure mailStructure) throws MessagingException, UnsupportedEncodingException {
       // return new ResponseEntity<>(mailStructure.getMessage() + " " +  mailStructure.getEmail(), HttpStatus.OK);
        return userService.forgetPassword(mailStructure);
    }

    @PostMapping("/{userId}/like/{noteId}")
    public ResponseEntity<?> likeNote(@PathVariable String userId, @PathVariable int noteId, @AuthenticationPrincipal SecurityUser securityUser){
        return userService.likeNote(userId, noteId, securityUser);
    }

    @DeleteMapping("/{userId}/unlike/{noteId}")
    public ResponseEntity<?> unlikeNote(@PathVariable String userId, @PathVariable int noteId, @AuthenticationPrincipal SecurityUser securityUser){
        return userService.unlikeNote(userId, noteId, securityUser);
    }

    @PostMapping("/{userId}/save/{noteId}")
    public ResponseEntity<?> saveNote(@PathVariable String userId, @PathVariable int noteId, @AuthenticationPrincipal SecurityUser securityUser){
        return userService.saveNote(userId, noteId, securityUser);
    }

    @DeleteMapping("/{userId}/unsave/{noteId}")
    public ResponseEntity<?> unsaveNote(@PathVariable String userId, @PathVariable int noteId, @AuthenticationPrincipal SecurityUser securityUser){
        return userService.unsaveNote(userId, noteId, securityUser);
    }

    @GetMapping("/saved/{userId}")
    public ResponseEntity<BasicMessageResponse> getSavedNotes(@PathVariable String userId, @AuthenticationPrincipal SecurityUser securityUser){
        return userService.getSavedNotes(userId, securityUser);
    }

    @GetMapping("/{userId}/followingPage")
    public ResponseEntity<BasicMessageResponse> getFollowingNotes(@PathVariable String userId, @AuthenticationPrincipal SecurityUser securityUser){
        return userService.getFollowingNotes(userId, securityUser);
    }

    @GetMapping("/followersCount/{userId}")
    public ResponseEntity<BasicMessageResponse> getFollowersCount(@PathVariable String userId){
        return userService.getFollowersCount(userId);
    }

    @GetMapping("/followingCount/{userId}")
    public ResponseEntity<BasicMessageResponse> getFollowingCount(@PathVariable String userId){
        return userService.getFollowingCount(userId);
    }

    @GetMapping("/does/{userAId}/follow/{userBId}")
    public Boolean isFollowing(@PathVariable String userAId,@PathVariable String userBId){
        return userService.isFollowing(userAId, userBId);
    }

//    @GetMapping("/redis/{key}")
//    public String getToke(@PathVariable String key){
//        try {
//            Token2 t = userService.getToken(key);
//            return t!=null?t.getId():"null";
//           // return t.getId() + " " + t.isBlacklisted();
//        }
//        catch(Exception e){
//             e.getMessage();
//        }
//        return null;
//    }
//
//    @PostMapping("/redis")
//    public String setToken(@RequestBody Token2 token){
//        try {
//            return userService.setToken(token);
//        }
//        catch(Exception e){
//            return e.getMessage();
//        }
//    }
}