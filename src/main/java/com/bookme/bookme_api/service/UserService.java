package com.bookme.bookme_api.service;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookme.bookme_api.dto.user.UserRequestDTO;
import com.bookme.bookme_api.dto.user.UserResponseDTO;
import com.bookme.bookme_api.entity.UserEntity;
import com.bookme.bookme_api.exception.DuplicateResourceException;
import com.bookme.bookme_api.exception.InvalidOperationException;
import com.bookme.bookme_api.exception.ResourceNotFoundException;
import com.bookme.bookme_api.mapper.UserMapper;
import com.bookme.bookme_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;


    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto){

        if(userRepo.findByEmail(dto.getEmail()).isPresent()){
            throw new DuplicateResourceException("Email already exists");

        }
        
        UserEntity entity = userMapper.toEntity(dto);
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


    @Transactional
   public UserResponseDTO update(Long id, UserRequestDTO dto){
    
        UserEntity entity = userRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!entity.isActive()){
            throw new InvalidOperationException("User is not active");
        }
        Optional<UserEntity> emailUser = userRepo.findByEmail(dto.getEmail());

        if(emailUser.isPresent() && !emailUser.get().getId().equals(id)){
            throw new DuplicateResourceException("email already exists");
        }
        

        entity.setName(dto.getName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());

       
        UserEntity updated = userRepo.save(entity);
        return userMapper.toResponseDTO(updated);
        
   }

   @Transactional
   public void deactivate(Long id){
        UserEntity entity = userRepo.findById(id).
        orElseThrow(()-> new ResourceNotFoundException("User not found"));
        
        if(!entity.isActive()){
            throw new InvalidOperationException("User already deactivated");
            }
        entity.setActive(false);
        userRepo.save(entity);
   }




}
