package com.example.zh2t.Mappers;

import com.example.zh2t.DTOs.NoteDTO;
import com.example.zh2t.DTOs.UserDTOG;
import com.example.zh2t.DTOs.UserDTOP;
import com.example.zh2t.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    public UserDTOG mapUserToUserDTOG(User user, boolean withPassword, boolean withDetails) {
        Set<NoteDTO> noteDTOs = user.getNotes().stream()
                .map(note -> modelMapper.map(note, NoteDTO.class))
                .collect(Collectors.toSet());

        Set<UserDTOP> followerDTOs = user.getFollowers().stream()
                .map(follower -> {
                    UserDTOP userDTOP = modelMapper.map(follower, UserDTOP.class);
                    if(!withPassword)
                        userDTOP.setPassword(null);
                    return userDTOP;
                })
                .collect(Collectors.toSet());

        Set<UserDTOP> followingDTOs = user.getFollowing().stream()
                .map(following -> {
                    UserDTOP userDTOP = modelMapper.map(following, UserDTOP.class);
                    if(!withPassword)
                        userDTOP.setPassword(null);
                    return userDTOP;
                })
                .collect(Collectors.toSet());

        if(!withDetails){
            noteDTOs = null;
            followerDTOs=null;
            followingDTOs=null;
        }

        return new UserDTOG(user.getUniqueName(), user.getEmail(), user.getBio(),
                user.getBirthDate(), noteDTOs, followerDTOs, followingDTOs);
    }
}
