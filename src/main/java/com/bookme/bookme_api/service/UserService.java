package com.bookme.bookme_api.service;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bookme.bookme_api.dto.user.UserRequestDTO;
import com.bookme.bookme_api.dto.user.UserResponseDTO;
import com.bookme.bookme_api.entity.UserEntity;
import com.bookme.bookme_api.enums.Role;
import com.bookme.bookme_api.exception.DuplicateResourceException;
import com.bookme.bookme_api.exception.ResourceNotFoundException;
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
            throw new DuplicateResourceException("Email already exists");

        }
        
        UserEntity entity = userMapper.toEntity(dto);
        entity.setRole(Role.CLIENTE);
        entity.setActive(true);
        UserEntity inserted = userRepo.save(entity);
        return userMapper.toResponseDTO(inserted);
    }



   public UserResponseDTO getById(Long id){
        UserEntity entity = userRepo.findById(id).
        orElseThrow(()-> new ResourceNotFoundException(
            "User not found"));
        
            return userMapper.toResponseDTO(entity);

   }



   public Page<UserResponseDTO> getAll(Pageable pageable) {

        return userRepo.findByActiveTrue(pageable)
            .map(userMapper::toResponseDTO);
        }

   public UserResponseDTO update(Long id, UserRequestDTO dto){
    
        UserEntity entity = userRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!entity.isActive()){
            throw new ResourceNotFoundException("User is not active");
        }
        Optional<UserEntity> emailUser = userRepo.findByEmail(dto.getEmail());

        if(emailUser.isPresent() && !emailUser.get().getId().equals(id)){
            throw new DuplicateResourceException("email already exists");
        }
        //recordar que el repositorio no tiene un update

        entity.setName(dto.getName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());

        //Acá ya hago el save y guardo el retorno
        UserEntity updated = userRepo.save(entity);
        return userMapper.toResponseDTO(updated);
        
   }

   public void deactivate(Long id){
        UserEntity entity = userRepo.findById(id).
        orElseThrow(()-> new ResourceNotFoundException("User not found"));
        //cambio el estado a inactivo
        if(!entity.isActive()){
            throw new ResourceNotFoundException("User already deactivated");
            }
        entity.setActive(false);
        userRepo.save(entity);
   }




}
