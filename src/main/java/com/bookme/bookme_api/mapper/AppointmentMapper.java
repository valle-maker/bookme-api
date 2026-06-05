package com.bookme.bookme_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookme.bookme_api.dto.appointment.AppointmentRequestDTO;
import com.bookme.bookme_api.dto.appointment.AppointmentResponseDTO;
import com.bookme.bookme_api.entity.AppointmentEntity;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "barber", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "startDateTime", expression = "java(dto.getAppointmentDate().atTime(dto.getStartTime()))")
    @Mapping(target = "endDateTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    AppointmentEntity toEntity(AppointmentRequestDTO dto);

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientName", source = "client.name")
    @Mapping(target = "barberId", source = "barber.id")
    @Mapping(target = "barberName", source = "barber.user.name")
    @Mapping(target = "serviceId", source = "service.id")
    @Mapping(target = "serviceName", source = "service.name")
    @Mapping(target = "appointmentDate", expression = "java(entity.getStartDateTime().toLocalDate())")
    @Mapping(target = "startTime", expression = "java(entity.getStartDateTime().toLocalTime())")
    @Mapping(target = "endTime", expression = "java(entity.getEndDateTime().toLocalTime())")
    AppointmentResponseDTO toResponseDTO(AppointmentEntity entity);
}
