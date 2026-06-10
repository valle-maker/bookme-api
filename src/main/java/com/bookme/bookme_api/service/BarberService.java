package com.bookme.bookme_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookme.bookme_api.dto.barber.BarberRequestDTO;
import com.bookme.bookme_api.dto.barber.BarberResponseDTO;
import com.bookme.bookme_api.entity.BarberEntity;
import com.bookme.bookme_api.entity.UserEntity;
import com.bookme.bookme_api.enums.Role;
import com.bookme.bookme_api.exception.DuplicateResourceException;
import com.bookme.bookme_api.exception.InvalidOperationException;
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


    @Transactional
    public BarberResponseDTO create(BarberRequestDTO dto){
        UserEntity userEntity = userRepo.findById(dto.getUserId()).
        orElseThrow(()-> new ResourceNotFoundException("User not found"));

        if(!userEntity.isActive()){
            throw new InvalidOperationException("User unavailable"); 
        }

        if(userEntity.getRole() != Role.BARBER){
            throw new InvalidOperationException("User is not a barber"); 
        }
        if(barberRepo.findByUser(userEntity).isPresent()){
            throw new DuplicateResourceException(
                "This user is already a barber");
            }
        if(!dto.getWorkStartTime().isBefore(dto.getWorkEndTime())){
            throw new InvalidOperationException( 
            "Start time must be before end time");
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

    public Page<BarberResponseDTO> getAllActive(Pageable pageable){
        return barberRepo.findByActiveTrue(pageable)
            .map(barberMapper::toResponseDTO);
    }

    @Transactional
    public BarberResponseDTO update(Long id, BarberRequestDTO dto){
        BarberEntity entity = barberRepo.findById(id).
            orElseThrow(()-> new ResourceNotFoundException("Barber not found"));
        
        if(!entity.isActive()){
            throw new InvalidOperationException("Barber is not active");
        }
        if(!dto.getWorkStartTime().isBefore(dto.getWorkEndTime())){
        throw new InvalidOperationException(
            "Start time must be before end time");
            }

        entity.setSpecialities(dto.getSpecialities());
        entity.setWorkStartTime(dto.getWorkStartTime());
        entity.setWorkEndTime(dto.getWorkEndTime());

        BarberEntity updated = barberRepo.save(entity);

        return barberMapper.toResponseDTO(updated);

    }

    @Transactional
    public void deactivate(Long id){
        BarberEntity entity = barberRepo.findById(id).
            orElseThrow(()-> new ResourceNotFoundException("Barber not found"));
        
        if(!entity.isActive()){
            throw new InvalidOperationException("Barber already deactivated");
        }

        entity.setActive(false);
        barberRepo.save(entity);
    }

}
