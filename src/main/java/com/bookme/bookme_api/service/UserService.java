package com.bookme.bookme_api.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookme.bookme_api.dto.profile.ChangePasswordRequestDTO;
import com.bookme.bookme_api.dto.profile.ProfileUpdateRequestDTO;
import com.bookme.bookme_api.dto.user.UserRequestDTO;
import com.bookme.bookme_api.dto.user.UserResponseDTO;
import com.bookme.bookme_api.entity.UserEntity;
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
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO create(UserRequestDTO dto){
        if(userRepo.findByEmail(dto.getEmail()).isPresent()){
            throw new InvalidOperationException("Email already in use");
        }
        UserEntity entity = userMapper.toEntity(dto);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setActive(true);
        UserEntity saved = userRepo.save(entity);
        return userMapper.toResponseDTO(saved);
    }

    public UserResponseDTO getById(Long id){
        UserEntity entity = userRepo.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        return userMapper.toResponseDTO(entity);
    }

    public Page<UserResponseDTO> getAll(Pageable pageable, Boolean onlyInactive) {
        if (Boolean.TRUE.equals(onlyInactive)) {
            return userRepo.findByActiveFalse(pageable)
                    .map(userMapper::toResponseDTO);
        }
        return userRepo.findByActiveTrue(pageable)
                .map(userMapper::toResponseDTO);
    }

    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO dto){
        UserEntity entity = userRepo.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        Optional<UserEntity> emailUser = userRepo.findByEmail(dto.getEmail());
        if(emailUser.isPresent() && !emailUser.get().getId().equals(id)){
            throw new InvalidOperationException("Email already in use");
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
        UserEntity entity = userRepo.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        if(!entity.isActive()){
            throw new InvalidOperationException("User already deactivated");
        }
        entity.setActive(false);
        userRepo.save(entity);
   }

   @Transactional
   public void activate(Long id){
        UserEntity entity = userRepo.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        if(entity.isActive()){
            throw new InvalidOperationException("User is already active");
        }
        entity.setActive(true);
        userRepo.save(entity);
   }

   @Transactional
   public UserResponseDTO updateMe(ProfileUpdateRequestDTO dto, String email){
    UserEntity entity = userRepo.findByEmail(email)
        .orElseThrow(()->new ResourceNotFoundException("User not found"));
        entity.setName(dto.getName());
        entity.setLastName(dto.getLastName());
        entity.setPhone(dto.getPhone());
        UserEntity updated = userRepo.save(entity);
        return userMapper.toResponseDTO(updated);
   }

   public UserResponseDTO getByEmail(String email){
    UserEntity entity = userRepo.findByEmail(email)
        .orElseThrow(()-> new ResourceNotFoundException("User not found"));
    return userMapper.toResponseDTO(entity);
   }

   @Transactional
   public void changePassword(String email, ChangePasswordRequestDTO dto) {
       UserEntity entity = userRepo.findByEmail(email)
           .orElseThrow(() -> new ResourceNotFoundException("User not found"));
       if (!passwordEncoder.matches(dto.getCurrentPassword(), entity.getPassword())) {
           throw new InvalidOperationException("La contraseña actual es incorrecta");
       }
       entity.setPassword(passwordEncoder.encode(dto.getNewPassword()));
       userRepo.save(entity);
   }
}
