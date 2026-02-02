package com.example.workflow.controller;

import com.example.workflow.repository.UserRepo;
import com.example.workflow.security.CustomUserDetails;
import com.example.workflow.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)   // ⭐ disables Spring Security filters
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean AuthenticationManager authManager;
    @MockitoBean JwtUtil jwt;
    @MockitoBean UserRepo repo;
    @MockitoBean PasswordEncoder encoder;

    @Test
    void register_shouldSaveUserAndReturnMessage() throws Exception {

        when(encoder.encode("pass123")).thenReturn("encodedPass");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "user":"john",
                              "pass":"pass123",
                              "role":"ROLE_USER"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("Registered"));

        verify(repo).save(argThat(user ->
                user.getUsername().equals("john") &&
                        user.getPassword().equals("encodedPass") &&
                        user.getRole().equals("ROLE_USER")
        ));
    }


    @Test
    void login_shouldAuthenticateAndReturnJwt() throws Exception {

        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        Collection<? extends GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_USER"));

        when(userDetails.getUsername()).thenReturn("john");

        doReturn(authorities).when(userDetails).getAuthorities();


        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        when(authManager.authenticate(any())).thenReturn(authentication);
        when(jwt.generate("john", "ROLE_USER")).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "user":"john",
                              "pass":"pass123"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("mocked-jwt-token"));

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwt).generate("john", "ROLE_USER");
    }
}
