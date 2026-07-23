package com.bookme.bookme_api.dto.user;



import com.bookme.bookme_api.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @Size(max= 100)
    @NotBlank
    private String phone;

    @NotNull
    private Role role;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

}
