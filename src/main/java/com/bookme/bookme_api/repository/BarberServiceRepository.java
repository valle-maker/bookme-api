package com.bookme.bookme_api.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookme.bookme_api.entity.BarberServiceEntity;

public interface BarberServiceRepository extends JpaRepository<BarberServiceEntity, Long>{
    Optional<BarberServiceRepository> findByActiveTrue();
    Page<BarberServiceEntity> findByActiveTrue(Pageable pageable);

}
