package com.example.zh2t.service;

import com.example.zh2t.BasicResponce.BasicMessageResponse;
import com.example.zh2t.DTOs.NoteDto2;
import com.example.zh2t.DTOs.UserDTOP;
import com.example.zh2t.models.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    public ResponseEntity<BasicMessageResponse> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication, SecurityUser securityUser);
    public ResponseEntity<?> getAllUsers(SecurityUser securityUser);
    public Optional<User> getUserById(String id);
    public ResponseEntity<?> getUserByIdR(String id);
    public ResponseEntity<BasicMessageResponse> addUser(UserDTOP user);
    public boolean isEmailAlreadyInUse(String email);
    public ResponseEntity<?> deleteUser(String id, SecurityUser securityUser);
    public ResponseEntity<?> followUser(String idA , String idB, SecurityUser securityUser);
    public ResponseEntity<?> getFollowers(String id);
    public ResponseEntity<?> getFollowing(String id);
    public ResponseEntity<?> unfollowUser(String idA, String idB, SecurityUser securityUser);
    public void save(User o);
    public ResponseEntity<?>patchUser(String id, Map<Object, Object> fields,  SecurityUser securityUser);

    public ResponseEntity<?>addNoteToUser(String userId, Note note, SecurityUser securityUser);

    public ResponseEntity<?> getAllUserNotes(String userId);
    public ResponseEntity<?> authenticate(UserDTOP user);
    public Token2 getToken(String key);
    public String setToken(Token2 token);
    public ResponseEntity<?> forgetPassword(MailStructure mailStructure) throws MessagingException, UnsupportedEncodingException;

    public ResponseEntity<BasicMessageResponse> getFollowingNotes(String userId, SecurityUser securityUser);

    public ResponseEntity<?> likeNote(String userId , int noteId, SecurityUser securityUser);
    public ResponseEntity<?> unlikeNote(String userId, int noteId, SecurityUser securityUser);

    public ResponseEntity<BasicMessageResponse> unsaveNote(String userId, int noteId, SecurityUser securityUser);
    public ResponseEntity<BasicMessageResponse> saveNote(String userId, int noteId, SecurityUser securityUser);
    public ResponseEntity<BasicMessageResponse> getSavedNotes(String userId, SecurityUser securityUser);
    public ResponseEntity<BasicMessageResponse> getFollowersCount(String userId);
    public ResponseEntity<BasicMessageResponse> getFollowingCount(String userId);
    public Boolean isFollowing(String userAId, String userBId);
    public boolean doesUserLikeNote(String uniqueName, int noteId);
    public boolean doesUserSaveNote(String uniqueName, int noteId);
    public List<UserDTOP> findUsersWithUniqueNameLike(String uniqueName);
    public List<NoteDto2> notesLikedByUser(String uniquename);
    public boolean isFollowedBy(String userA, String userB);
}
