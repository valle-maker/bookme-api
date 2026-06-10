package com.bookme.bookme_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookme.bookme_api.dto.barber.BarberRequestDTO;
import com.bookme.bookme_api.dto.barber.BarberResponseDTO;
import com.bookme.bookme_api.entity.BarberEntity;


@Mapper(componentModel = "spring")
public interface BarberMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    
    
    BarberEntity toEntity(BarberRequestDTO dto);
    @Mapping(target = "barberName", source = "user.name")
    BarberResponseDTO toResponseDTO(BarberEntity entity); 

}
