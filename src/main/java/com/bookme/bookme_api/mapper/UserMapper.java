package com.bookme.bookme_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookme.bookme_api.dto.user.UserRequestDTO;
import com.bookme.bookme_api.dto.user.UserResponseDTO;
import com.bookme.bookme_api.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    UserEntity toEntity(UserRequestDTO dto);

    UserResponseDTO toResponseDTO(UserEntity entity);
}
