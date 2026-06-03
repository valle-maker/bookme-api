package com.bookme.bookme_api.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookme.bookme_api.entity.BarberEntity;
import com.bookme.bookme_api.entity.UserEntity;




public interface BarberRepository extends JpaRepository<BarberEntity, Long>{

    Page<BarberEntity>  findByActiveTrue(Pageable pageable);
    Optional<BarberEntity> findByUser(UserEntity user);

}
