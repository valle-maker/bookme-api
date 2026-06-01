package com.bookme.bookme_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookme.bookme_api.entity.BarberEntity;
import java.util.List;


public interface BarberRepository extends JpaRepository<BarberEntity, Long>{

    Optional<BarberEntity>  findByActiveTrue();

}
