package com.bookme.bookme_api.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookme.bookme_api.entity.BarberServiceEntity;

public interface BarberServiceRepository extends JpaRepository<BarberServiceEntity, Long>{
    
    Page<BarberServiceEntity> findByActiveTrue(Pageable pageable);

}
