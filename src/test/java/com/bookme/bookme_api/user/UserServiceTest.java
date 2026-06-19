package com.bookme.bookme_api.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookme.bookme_api.dto.user.UserRequestDTO;
import com.bookme.bookme_api.entity.UserEntity;
import com.bookme.bookme_api.enums.Role;
import com.bookme.bookme_api.exception.DuplicateResourceException;
import com.bookme.bookme_api.mapper.UserMapper;
import com.bookme.bookme_api.repository.UserRepository;
import com.bookme.bookme_api.service.UserService;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
private UserMapper userMapper;


    @InjectMocks
    private UserService userService;
    private UserEntity user;
    private UserRequestDTO dto;

    @BeforeEach
    void setUp(){
        user = new UserEntity();
        user.setId(1L);
        user.setName("Carlos");
        user.setEmail("carlos@mail.com");
        user.setRole(Role.CLIENT);
        user.setActive(true);

        dto = new UserRequestDTO();
        dto.setEmail("newEmail@mail.com");
        

    }

    @Test
    void update_ShouldThrowException_WhenEmailAlreadyExists(){

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setName("Juan");
        user2.setEmail("newEmail@mail.com");
        user2.setRole(Role.CLIENT);
        user2.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user2));

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            ()-> userService.update(1L, dto));
        
        assertEquals("email already exists",
            exception.getMessage());
        verify(userRepository, never()).save(any());


        
    }

}
