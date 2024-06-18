package com.example.zh2t.service;

import com.example.zh2t.BasicResponce.BasicMessageResponse;
import com.example.zh2t.DTOs.NoteDTO;
import com.example.zh2t.DTOs.NoteDto2;
import com.example.zh2t.DTOs.UserDTOG;
import com.example.zh2t.DTOs.UserDTOP;
import com.example.zh2t.Mappers.EntityDtoConverter;
import com.example.zh2t.Mappers.UserMapper;
import com.example.zh2t.exceptions.ObjectNotFoundException;
import com.example.zh2t.models.*;
import com.example.zh2t.repositories.NoteRepository;
import com.example.zh2t.repositories.UserRepository;
import com.example.zh2t.validator.ObjectsValidator;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.persistence.Transient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private  JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    NoteService noteService;

    ObjectsValidator validator = new ObjectsValidator();

    @Autowired
    private EntityDtoConverter entityDtoConverter;

    @Autowired
    UserMapper userMapper;

    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    @Autowired
    MailService mailService;

    @Autowired
    NoteRepository noteRepository;

    @Resource(name="redisTemplate")
    private HashOperations<String, String, Token2> hashOperations;

    public Token2 getToken(String key){
        return hashOperations.get("Token2", key);
    }

    public String setToken(Token2 token){
        hashOperations.put("Token2", token.getId(), token);
        return "DONE";
    }

    public ResponseEntity<?> getUserByIdR(String id) {
        Optional<User> user = getUserById(id);
        if(user.isPresent()){
            try {
                UserDTOG u = userMapper.mapUserToUserDTOG(user.get(), false, true);
                return new ResponseEntity<>(u, HttpStatus.OK);
            } catch (MappingException ex) {
                return  new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return  new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> getAllUsers(SecurityUser securityUser){
        if(!securityUser.getRole().equals("ROLE_ADMIN"))
            return new ResponseEntity<>(new BasicMessageResponse("error", "access denied", 403), HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(userRepo.findAll()
                .stream()
                .map(user -> userMapper.mapUserToUserDTOG(user, false, true))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    public Optional<User> getUserById(String id){
        return userRepo.findById(id);
    }

    public boolean isEmailAlreadyInUse(String email) {
        User user = userRepo.getUserByEmail(email);
        return user != null;
    }

    public ResponseEntity<BasicMessageResponse> addUser(UserDTOP user){
        if(getUserById(user.getUniqueName()).isPresent() || isEmailAlreadyInUse(user.getEmail()))
            return new ResponseEntity<>(new BasicMessageResponse("error", "Username or email already used", 409), HttpStatus.CONFLICT);
        validator.validate(entityDtoConverter.convertToEntity(user, User.class));
        userRepo.save(entityDtoConverter.convertToEntity(user, User.class));
        String jwt = jwtService.generateToken(user);
        setToken(new Token2(jwt, false));
        return new ResponseEntity<>(new BasicMessageResponse("token", jwt, 201),  HttpStatus.CREATED);
    }

    public ResponseEntity<?> authenticate(UserDTOP user){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUniqueName(),
                        user.getPassword()
                )
        );
        User x = userRepo.findById(user.getUniqueName()).orElseThrow();
        String token = jwtService.generateToken(entityDtoConverter.convertToEntity(x, UserDTOP.class));
        setToken(new Token2(token, false));
        return new ResponseEntity<>(new BasicMessageResponse("token", token, 200), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> forgetPassword(MailStructure mailStructure) throws MessagingException, UnsupportedEncodingException {
        User user = userRepo.getUserByEmail(mailStructure.getEmail());
        if(user == null)
            return new ResponseEntity<>(new BasicMessageResponse("error", "invalid email", 404), HttpStatus.NOT_FOUND);
        try {
            MailStructure m = new MailStructure();
            m.setEmail(user.getEmail());
            m.setSubject("UserNoteApp Credentials");
            m.setMessage("your password is: "+user.getPassword() + "\nplease do not share it!");
            mailService.sendMail(m);
        }catch (Exception e) {return  new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);}
        return new ResponseEntity<>("check this email: " + user.getEmail(), HttpStatus.OK);
    }

    public ResponseEntity<BasicMessageResponse> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication, SecurityUser securityUser) {
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ") || !jwtService.isValid( authHeader.substring(7), securityUser) || !jwtService.extractUsername(authHeader.substring(7)).equals(securityUser.getUsername()))
            return new ResponseEntity<>(new BasicMessageResponse("error", "invalid token", 403), HttpStatus.FORBIDDEN);
        String token = authHeader.substring(7);
        Token2 storedToken = getToken(token);

        if(storedToken != null) {
            setToken(new Token2(token, true));
        }
        return new ResponseEntity<>(new BasicMessageResponse("message", "logged out successfully", 200), HttpStatus.OK);
    }

    @Transactional
    public  ResponseEntity<?> deleteUser(String id, SecurityUser securityUser){
        Optional<User> user = userRepo.findById(id);
        if(securityUser.getRole().equals("ROLE_ADMIN") || securityUser.getUsername().equals(id)) {
            if (user.isPresent()) {
                Set<User> follower = user.get().getFollowers();
                follower.forEach(user1 -> {
                    user1.getFollowing().remove(user.get());
                    userRepo.save(user1);
                });
                Set<User> following = user.get().getFollowing();
                following.forEach(user1 -> {
                    user1.getFollowers().remove(user.get());
                    userRepo.save(user1);
                });
                user.get().getLikedNotes().forEach(note -> {
                    user.get().getLikedNotes().remove(note);
                    userRepo.save(user.get());
                });
                user.get().getSavedNotes().forEach(note -> {
                    user.get().getSavedNotes().remove(note);
                    userRepo.save(user.get());
                });
                follower.clear();
                following.clear();
            //    user.get().getLikedNotes().clear();
              //  user.get().getSavedNotes().clear();
                List<Note> userNotes = noteService.getNotesByUserId(id);
                if (!userNotes.isEmpty())
                    userNotes.forEach(noteService::deleteNote);
                userNotes.clear();
                userRepo.save(user.get());
                userRepo.delete(user.get());
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return  new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<?> followUser(String idA , String idB, SecurityUser securityUser) {
        if(!securityUser.getUsername().equals(idA))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        Optional<User> userA = userRepo.findById(idA);
        Optional<User> userB = userRepo.findById(idB);
        if(!userB.isPresent() || ! userA.isPresent() || idA.equals(idB))
            return  new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        userA.get().getFollowing().add(userB.get());
        userRepo.save(userA.get());
        userB.get().getFollowers().add(userA.get());
        userRepo.save(userB.get());
        return new ResponseEntity<>( HttpStatus.CREATED);
    }

    public ResponseEntity<?> likeNote(String userId , int noteId, SecurityUser securityUser) {
        User user  = getUserById(userId).orElseThrow(() -> new ObjectNotFoundException("User is not in the database"));
        if(!securityUser.getUsername().equals(user.getUniqueName()))
            return new ResponseEntity<>(new BasicMessageResponse("error", "Forbidden", 403), HttpStatus.FORBIDDEN);
        Note note = noteService.getNoteBId(noteId).orElseThrow(() -> new ObjectNotFoundException("note is not in the database"));
        user.getLikedNotes().add(note);
        userRepo.save(user);
        return new ResponseEntity<>(new BasicMessageResponse("Message","Liked post successfully",201), HttpStatus.CREATED);
    }

    public ResponseEntity<BasicMessageResponse> saveNote(String userId, int noteId, SecurityUser securityUser){
        User user  = getUserById(userId).orElseThrow(() -> new ObjectNotFoundException("User is not in the database"));
        if(!securityUser.getUsername().equals(user.getUniqueName()))
            return new ResponseEntity<>(new BasicMessageResponse("error", "Forbidden", 403), HttpStatus.FORBIDDEN);
        Note note = noteService.getNoteBId(noteId).orElseThrow(() -> new ObjectNotFoundException("note is not in the database"));
        user.getSavedNotes().add(note);
        userRepo.save(user);
        return new ResponseEntity<>(new BasicMessageResponse("Message", "saved", 201), HttpStatus.CREATED);
    }

    public ResponseEntity<?> getFollowers(String id) {
        Optional<User> user = userRepo.findById(id);
        if(!user.isPresent())
            return  new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(user.get().getFollowers().stream().map(user1 -> userMapper.mapUserToUserDTOG(user1, false, false)).collect(Collectors.toList()), HttpStatus.OK);
    }

    public ResponseEntity<BasicMessageResponse> getSavedNotes(String userId, SecurityUser securityUser){
        User user  = getUserById(userId).orElseThrow(() -> new ObjectNotFoundException("User is not in the database"));
        if(!securityUser.getUsername().equals(user.getUniqueName()))
            return new ResponseEntity<>(new BasicMessageResponse("error", "Forbidden", 403), HttpStatus.FORBIDDEN);
        Set<NoteDto2> noteDTOS = user.getSavedNotes().stream().map(note -> {
                    NoteDto2 x = entityDtoConverter.convertToEntity(note, NoteDto2.class);
                    x.setUniqueName(note.getUser().getUniqueName());
                    return x;
                }).collect(Collectors.toSet());
        return new ResponseEntity<>(new BasicMessageResponse("saved", noteDTOS, 200), HttpStatus.OK);
    }

    public ResponseEntity<?> getFollowing(String id) {
        Optional<User> user = userRepo.findById(id);
        if(!user.isPresent())
            return  new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(user.get().getFollowing().stream().map(user1 -> userMapper.mapUserToUserDTOG(user1, false, false)).collect(Collectors.toList()), HttpStatus.OK);
    }

    public boolean doesUserLikeNote(String uniqueName, int noteId){
        User user = getUserById(uniqueName).get();
        Set<Note> likedNotes = user.getLikedNotes();
        Note note = noteService.getNoteBId(noteId).get();
        return likedNotes.contains(note);
    }

    public boolean doesUserSaveNote(String uniqueName, int noteId){
        User user = getUserById(uniqueName).get();
        Set<Note> likedNotes = user.getSavedNotes();
        Note note = noteService.getNoteBId(noteId).get();
        return likedNotes.contains(note);
    }

    public List<UserDTOP> findUsersWithUniqueNameLike(String uniqueName){
        List<UserDTOP> users = userRepo.findUsersWithUniqueNameLike(uniqueName).stream().map(
                user -> {
                    UserDTOP x = entityDtoConverter.convertToDto(user, UserDTOP.class);
                    return x;
                }
        ).collect(Collectors.toList());
        return users;
    }

    public ResponseEntity<?> unfollowUser(String idA, String idB, SecurityUser securityUser) {
        if(!securityUser.getUsername().equals(idA))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        Optional<User> userA = userRepo.findById(idA);
        Optional<User> userB = userRepo.findById(idB);
        if(!userB.isPresent() || ! userA.isPresent() || idA.equals(idB))
            return  new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        userA.get().getFollowing().remove(userB.get());
        userB.get().getFollowers().remove(userA.get());

        userRepo.save(userA.get());
        userRepo.save(userB.get());

        return new ResponseEntity<>( HttpStatus.OK);
    }
    public ResponseEntity<?> unlikeNote(String userId, int noteId, SecurityUser securityUser){
        User user  = getUserById(userId).orElseThrow(() -> new ObjectNotFoundException("User is not in the database"));
        if(!securityUser.getUsername().equals(user.getUniqueName()))
            return new ResponseEntity<>(new BasicMessageResponse("error", "Forbidden", 403), HttpStatus.FORBIDDEN);
        Note note = noteService.getNoteBId(noteId).orElseThrow(() -> new ObjectNotFoundException("note is not in the database"));
        user.getLikedNotes().remove(note);
        userRepo.save(user);
        return new ResponseEntity<>(new BasicMessageResponse("Message","Unliked post successfully",201), HttpStatus.CREATED);
    }

    public ResponseEntity<BasicMessageResponse> unsaveNote(String userId, int noteId, SecurityUser securityUser){
        User user  = getUserById(userId).orElseThrow(() -> new ObjectNotFoundException("User is not in the database"));
        if(!securityUser.getUsername().equals(user.getUniqueName()))
            return new ResponseEntity<>(new BasicMessageResponse("error", "Forbidden", 403), HttpStatus.FORBIDDEN);
        Note note = noteService.getNoteBId(noteId).orElseThrow(() -> new ObjectNotFoundException("note is not in the database"));
        user.getSavedNotes().remove(note);
        userRepo.save(user);
        return new ResponseEntity<>(new BasicMessageResponse("Message","Unsaved note successfully",201), HttpStatus.CREATED);
    }
    public void save(User x){
        userRepo.save(x);
    }

    public ResponseEntity<?> patchUser(String id, Map<Object, Object> fields,  SecurityUser securityUser){
        if( securityUser.getRole().equals("ROLE_ADMIN") || securityUser.getUsername().equals(id)){
        Optional<User> user = userRepo.findById(id);
        if(user.isPresent()){
            fields.forEach((key, value)-> {
                if(key == "birthDate")
                    value = java.sql.Date.valueOf(value.toString());
                Field field = ReflectionUtils.findField(User.class, (String)key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, user.get(), value);
            });
            validator.validate(user.get());
            userRepo.save(user.get());
            return new ResponseEntity<>(userMapper.mapUserToUserDTOG(user.get(), true, false), HttpStatus.OK);
        }
        return  new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        else
            return new ResponseEntity<>("id: " + id + " security pricipal body:" + securityUser.getUsername(), HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<?>addNoteToUser(String userId, Note note, SecurityUser securityUser) {
        if(!securityUser.getUsername().equals(userId))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        validator.validate(note);
        Optional<User> user = getUserById(userId);

        if(user.isPresent()) {
            note.setUser(user.get());
            noteService.addNote(note);
            return new ResponseEntity<>(entityDtoConverter.convertToDto(note, NoteDTO.class), HttpStatus.CREATED);
       }
        return  new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    //get a user's notes
    public ResponseEntity<?> getAllUserNotes(String userId) {
        Optional<User> user = getUserById(userId);
        if (user.isPresent()) {
            List<Note> notes = noteService.getNotesByUserId(userId);
            List<NoteDto2> noteDtos = notes.stream()
                    .map(note -> {NoteDto2 x = entityDtoConverter.convertToDto(note, NoteDto2.class); x.setUniqueName(userId); return  x;})
                    .collect(Collectors.toList());
            return new ResponseEntity<>(noteDtos, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    public ResponseEntity<BasicMessageResponse> getFollowingNotes(String userId, SecurityUser securityUser){
        User user  = getUserById(userId).orElseThrow(() -> new ObjectNotFoundException("User is not in the database"));
        if(!securityUser.getUsername().equals(user.getUniqueName()))
            return new ResponseEntity<>(new BasicMessageResponse("error", "Forbidden", 403), HttpStatus.FORBIDDEN);
        Set<User> following = user.getFollowing();
        List<NoteDto2> noteDTOS = following.stream()
                .flatMap(uu -> uu.getNotes().stream())
                .map(note -> {
                   note.getUser().setPassword(null);
                   NoteDto2 x =  entityDtoConverter.convertToDto(note, NoteDto2.class);
                   x.setUniqueName(note.getUser().getUniqueName());
                   return x;
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(new BasicMessageResponse("notes", noteDTOS, 200), HttpStatus.OK);
    }

    public ResponseEntity<BasicMessageResponse> getFollowersCount(String userId){
        return new ResponseEntity<>(new BasicMessageResponse("followers",getUserById(userId).get().getFollowers().size(),200), HttpStatus.OK);
    }

    public ResponseEntity<BasicMessageResponse> getFollowingCount(String userId){
        return new ResponseEntity<>(new BasicMessageResponse("following",getUserById(userId).get().getFollowing().size(),200), HttpStatus.OK);
    }

    //Does A follow B?
    public Boolean isFollowing(String userAId, String userBId){
        User userA  = getUserById(userAId).orElseThrow(() -> new ObjectNotFoundException("User is not in the database"));
        User userB  = getUserById(userBId).orElseThrow(() -> new ObjectNotFoundException("User is not in the database"));
        return userA.getFollowing().contains(userB);
    }
}