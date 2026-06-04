package com.bookme.bookme_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import com.bookme.bookme_api.dto.barberService.BarberServiceRequestDTO;
import com.bookme.bookme_api.dto.barberService.BarberServiceResponseDTO;
import com.bookme.bookme_api.entity.BarberServiceEntity;
import com.bookme.bookme_api.exception.InvalidOperationException;
import com.bookme.bookme_api.exception.ResourceNotFoundException;
import com.bookme.bookme_api.mapper.BarberServiceMapper;
import com.bookme.bookme_api.repository.BarberServiceRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class ServiceService {

    private final BarberServiceRepository serviceRepo;
    private final BarberServiceMapper serviceMapper;

    public BarberServiceResponseDTO create(BarberServiceRequestDTO dto){
        BarberServiceEntity entity = serviceMapper.toEntity(dto);
        entity.setActive(true);

        BarberServiceEntity created = serviceRepo.save(entity);
        return serviceMapper.toResponseDTO(created);

    }

    public BarberServiceResponseDTO getById(Long id){
        BarberServiceEntity entity = serviceRepo.findById(id).
            orElseThrow(()-> new ResourceNotFoundException("Service not found"));
            return serviceMapper.toResponseDTO(entity);

    }
     
    public Page<BarberServiceResponseDTO> getAllActive(int page, int size){
    
    PageRequest pageable = PageRequest.of(page, size);

    return serviceRepo.findByActiveTrue(pageable)
            .map(serviceMapper::toResponseDTO);
}

    public BarberServiceResponseDTO update(Long id, BarberServiceRequestDTO dto){
        BarberServiceEntity entity = serviceRepo.findById(id).
            orElseThrow(()-> new ResourceNotFoundException("Service not found"));
        if(!entity.isActive()){
            throw new InvalidOperationException("Service is not active");
        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDurationMinutes(dto.getDurationMinutes());
        entity.setPrice(dto.getPrice());

        BarberServiceEntity updated = serviceRepo.save(entity);
        return serviceMapper.toResponseDTO(updated);
    }

    public void deactivate(Long id){
        BarberServiceEntity entity = serviceRepo.findById(id).
            orElseThrow(()-> new ResourceNotFoundException("Service not found"));
        
        if(!entity.isActive()){
            throw new InvalidOperationException("Service already deactivated");
        }
        entity.setActive(false);
        serviceRepo.save(entity);
    }

}
