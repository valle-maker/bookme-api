package com.bookme.bookme_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookme.bookme_api.dto.barberService.BarberServiceRequestDTO;
import com.bookme.bookme_api.dto.barberService.BarberServiceResponseDTO;
import com.bookme.bookme_api.entity.BarberServiceEntity;

@Mapper(componentModel = "spring")
public interface BarberServiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    BarberServiceEntity toEntity(BarberServiceRequestDTO dto);

    BarberServiceResponseDTO toResponseDTO(BarberServiceEntity entity);
}