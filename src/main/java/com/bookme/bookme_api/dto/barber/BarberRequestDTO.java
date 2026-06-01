package com.bookme.bookme_api.dto.barber;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BarberRequestDTO {
    @NotNull
    private Long userId;

    @NotBlank
    @Size(max = 100)
    private String specialities;




}
