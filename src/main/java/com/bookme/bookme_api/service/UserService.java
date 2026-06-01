package com.bookme.bookme_api.service;

import org.springframework.stereotype.Service;

import com.bookme.bookme_api.dto.user.UserRequestDTO;
import com.bookme.bookme_api.dto.user.UserResponseDTO;
import com.bookme.bookme_api.entity.UserEntity;
import com.bookme.bookme_api.enums.Role;
import com.bookme.bookme_api.mapper.UserMapper;
import com.bookme.bookme_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;
    
    public UserResponseDTO createUser(UserRequestDTO dto){

        if(userRepo.findByEmail(dto.getEmail()).isPresent()){
            throw new RuntimeException("ya hay un usuario registrado con ese email");

        }
        
        UserEntity entity = userMapper.toEntity(dto);
        entity.setRole(Role.CLIENTE);
        //Solo seteo el rol, por que el id y la fecha se hacen de forma automatica
        UserEntity inserted = userRepo.save(entity);
        return userMapper.toResponseDTO(inserted);
    }

   


}
