package com.bookme.bookme_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BarberServiceRepository extends JpaRepository<BarberServiceRepository, Long>{
    Optional<BarberServiceRepository> findByActiveTrue();

}
