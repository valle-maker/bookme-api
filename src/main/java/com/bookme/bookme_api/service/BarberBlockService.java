package com.bookme.bookme_api.service;

import com.bookme.bookme_api.dto.barberblock.BarberBlockRequestDTO;
import com.bookme.bookme_api.dto.barberblock.BarberBlockResponseDTO;
import com.bookme.bookme_api.entity.BarberBlockEntity;
import com.bookme.bookme_api.entity.BarberEntity;
import com.bookme.bookme_api.exception.InvalidOperationException;
import com.bookme.bookme_api.exception.ResourceNotFoundException;
import com.bookme.bookme_api.repository.BarberBlockRepository;
import com.bookme.bookme_api.repository.BarberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BarberBlockService {

    private final BarberBlockRepository blockRepository;
    private final BarberRepository barberRepository;

    @Transactional
    public BarberBlockResponseDTO createBlock(String barberEmail, BarberBlockRequestDTO dto) {
        BarberEntity barber = barberRepository.findByUserEmail(barberEmail)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un perfil de barbero para este usuario"));

        if (dto.getEndDateTime().isBefore(dto.getStartDateTime()) ||
            dto.getEndDateTime().isEqual(dto.getStartDateTime())) {
            throw new InvalidOperationException("La hora de fin debe ser posterior a la hora de inicio");
        }
        if (dto.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidOperationException("No puedes bloquear un horario en el pasado");
        }

        BarberBlockEntity block = BarberBlockEntity.builder()
                .barber(barber)
                .startDateTime(dto.getStartDateTime())
                .endDateTime(dto.getEndDateTime())
                .reason(dto.getReason())
                .build();

        return toResponse(blockRepository.save(block));
    }

    public List<BarberBlockResponseDTO> getMyBlocks(String barberEmail) {
        BarberEntity barber = barberRepository.findByUserEmail(barberEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado"));

        return blockRepository
                .findByBarberIdAndStartDateTimeGreaterThanEqualOrderByStartDateTime(
                        barber.getId(), LocalDateTime.now())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BarberBlockResponseDTO> getBlocksForBarber(Long barberId, LocalDateTime from, LocalDateTime to) {
        return blockRepository
                .findByBarberIdAndStartDateTimeBetween(barberId, from, to)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteBlock(Long blockId, String barberEmail) {
        BarberBlockEntity block = blockRepository.findById(blockId)
                .orElseThrow(() -> new ResourceNotFoundException("Bloque no encontrado"));

        if (!block.getBarber().getUser().getEmail().equals(barberEmail)) {
            throw new InvalidOperationException("Solo puedes eliminar tus propios bloques");
        }

        blockRepository.delete(block);
    }

    private BarberBlockResponseDTO toResponse(BarberBlockEntity e) {
        return BarberBlockResponseDTO.builder()
                .id(e.getId())
                .barberId(e.getBarber().getId())
                .startDateTime(e.getStartDateTime())
                .endDateTime(e.getEndDateTime())
                .reason(e.getReason())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
