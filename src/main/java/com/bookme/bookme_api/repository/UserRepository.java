package com.bookme.bookme_api.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookme.bookme_api.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{

    Optional<UserEntity> findByEmail(String email);
    Page<UserEntity> findByActiveTrue(Pageable pageable);
    Page<UserEntity> findByActiveFalse(Pageable pageable);
}
