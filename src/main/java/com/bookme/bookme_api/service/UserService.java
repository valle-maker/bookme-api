package com.bookme.bookme_api.service;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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



   public UserResponseDTO getById(Long id){
        UserEntity entity = userRepo.findById(id).
        orElseThrow(()-> new RuntimeException(
            "Usuario no encontrado"));
        
            return userMapper.toResponseDTO(entity);

   }



   public Page<UserResponseDTO> getAll(int page, int size){
        Pageable pageable = PageRequest.of(page, size); 

        return userRepo.findAll(pageable).
        map(userMapper::toResponseDTO);
   }

   public UserResponseDTO update(Long id, UserRequestDTO dto){
    
        UserEntity entity = userRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Optional<UserEntity> emailUser = userRepo.findByEmail(dto.getEmail());

        if(emailUser.isPresent() && !emailUser.get().getId().equals(id)){
            throw new RuntimeException("No se puede actualizar el email, ya existe un usuario con ese email");
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

//Este método lo cambiaré para hacer un soft, poner el atributo de active en el aentidad
   /***  public void delete(Long id){
        UserEntity entity = userRepo.findById(id).
        orElseThrow(()-> new RuntimeException("User not found"));

        userRepo.delete(entity);
        
   }
*/

}
