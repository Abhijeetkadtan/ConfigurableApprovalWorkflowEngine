package com.example.workflow.controller;

import com.example.workflow.dto.AuthRequest;
import com.example.workflow.entity.UserEntity;
import com.example.workflow.repository.UserRepo;
import com.example.workflow.security.CustomUserDetails;
import com.example.workflow.security.JwtUtil;
import com.example.workflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwt;
    private final UserRepo repo;
    private final PasswordEncoder encoder;

    @PostMapping("/register")
    public String register(@RequestBody AuthRequest req){
        UserEntity u=new UserEntity();
        u.setUsername(req.getUser());
        u.setPassword(encoder.encode(req.getPass()));
        u.setRole(req.getRole());
        repo.save(u);
        return "Registered";
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest req){
        Authentication auth=authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUser(),req.getPass()));
        CustomUserDetails ud=(CustomUserDetails) auth.getPrincipal();
        return jwt.generate(ud.getUsername(),
                ud.getAuthorities().iterator().next().getAuthority());
    }

}
