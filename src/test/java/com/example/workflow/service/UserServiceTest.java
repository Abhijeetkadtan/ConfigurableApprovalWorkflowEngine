package com.example.workflow.service;

import com.example.workflow.entity.UserEntity;
import com.example.workflow.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo repo;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService service;


    @Test
    void register_shouldEncodePasswordAndSaveUser() {

        when(encoder.encode("pass123")).thenReturn("encodedPass");

        service.register("john", "pass123", "ROLE_USER");

        verify(repo).save(argThat(user ->
                user.getUsername().equals("john") &&
                        user.getPassword().equals("encodedPass") &&
                        user.getRole().equals("ROLE_USER")
        ));
    }


    @Test
    void login_shouldReturnUserWhenPasswordMatches() {

        UserEntity user = new UserEntity();
        user.setUsername("john");
        user.setPassword("encodedPass");
        user.setRole("ROLE_USER");

        when(repo.findByUsername("john")).thenReturn(Optional.of(user));
        when(encoder.matches("pass123", "encodedPass")).thenReturn(true);

        UserEntity result = service.login("john", "pass123");

        assertEquals("john", result.getUsername());
        verify(encoder).matches("pass123", "encodedPass");
    }


    @Test
    void login_shouldThrowWhenPasswordInvalid() {

        UserEntity user = new UserEntity();
        user.setUsername("john");
        user.setPassword("encodedPass");

        when(repo.findByUsername("john")).thenReturn(Optional.of(user));
        when(encoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.login("john", "wrongPass"));

        assertEquals("Invalid password", ex.getMessage());
    }


    @Test
    void login_shouldThrowWhenUserNotFound() {

        when(repo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(Exception.class,
                () -> service.login("unknown", "pass"));
    }
}
