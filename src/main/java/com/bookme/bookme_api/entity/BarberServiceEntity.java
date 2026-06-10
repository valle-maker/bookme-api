package com.bookme.bookme_api.entity;


import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "barber_services")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class BarberServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable =  false)
    private int durationMinutes;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active;


}
