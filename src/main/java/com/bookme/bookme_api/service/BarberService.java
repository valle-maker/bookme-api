package com.bookme.bookme_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.bookme.bookme_api.dto.barber.BarberRequestDTO;
import com.bookme.bookme_api.dto.barber.BarberResponseDTO;
import com.bookme.bookme_api.entity.BarberEntity;
import com.bookme.bookme_api.entity.UserEntity;
import com.bookme.bookme_api.enums.Role;
import com.bookme.bookme_api.exception.DuplicateResourceException;
import com.bookme.bookme_api.exception.ResourceNotFoundException;
import com.bookme.bookme_api.mapper.BarberMapper;
import com.bookme.bookme_api.repository.BarberRepository;
import com.bookme.bookme_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BarberService {

    private final BarberRepository barberRepo;
    private final BarberMapper barberMapper;
    private final UserRepository userRepo;

    public BarberResponseDTO create(BarberRequestDTO dto){
        UserEntity userEntity = userRepo.findById(dto.getUserId()).
        orElseThrow(()-> new ResourceNotFoundException("User not found"));

        if(!userEntity.isActive()){
            throw new RuntimeException("User disavaliable"); //Manejar bien esta excepción
        }

        if(userEntity.getRole() != Role.BARBER){
            throw new RuntimeException("User is not a barber"); //Manejar tambien esta excepcion
        }
        if(barberRepo.findByUser(userEntity).isPresent()){
            throw new DuplicateResourceException(
                "This user is already a barber");
            }
        if(dto.getWorkStartTime().isAfter(dto.getWorkEndTime())){
            throw new RuntimeException( //Manejar esta excepción
            "La hora de inicio debe ser anterior a la hora de fin");
            }
        BarberEntity barberEntity = barberMapper.toEntity(dto);
        barberEntity.setUser(userEntity);
        barberEntity.setActive(true);
        
        BarberEntity created = barberRepo.save(barberEntity);
        return barberMapper.toResponseDTO(created);

    }

    public BarberResponseDTO getById(Long id){
        BarberEntity entity = barberRepo.findById(id).
            orElseThrow(()-> new ResourceNotFoundException("Barber not found"));
            return barberMapper.toResponseDTO(entity);
    }

    public Page<BarberResponseDTO> getAllActive(int page, int size){
        PageRequest pageable = PageRequest.of(page, size);

        return barberRepo.findByActiveTrue(pageable)
            .map(barberMapper::toResponseDTO);
    }

    public BarberResponseDTO update(Long id, BarberRequestDTO dto){
        BarberEntity entity = barberRepo.findById(id).
            orElseThrow(()-> new ResourceNotFoundException("Barber not found"));
        
        if(!entity.isActive()){
            throw new ResourceNotFoundException("Barber is not active");
        }
        if(!dto.getWorkStartTime().isBefore(dto.getWorkEndTime())){
        throw new RuntimeException(
            "La hora de inicio debe ser anterior a la hora de fin");
            }

        entity.setSpecialities(dto.getSpecialities());
        entity.setWorkStartTime(dto.getWorkStartTime());
        entity.setWorkEndTime(dto.getWorkEndTime());

        BarberEntity updated = barberRepo.save(entity);

        return barberMapper.toResponseDTO(updated);

    }

    public void deactivate(Long id){
        BarberEntity entity = barberRepo.findById(id).
            orElseThrow(()-> new ResourceNotFoundException("Barber not found"));
        
        if(!entity.isActive()){
            throw new ResourceNotFoundException("User already deactivated");
        }

        entity.setActive(false);
        barberRepo.save(entity);
    }

}
