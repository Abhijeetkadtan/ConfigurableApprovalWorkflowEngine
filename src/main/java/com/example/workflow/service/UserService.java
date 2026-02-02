package com.example.workflow.service;

import com.example.workflow.entity.UserEntity;
import com.example.workflow.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo repo;
    private final PasswordEncoder encoder;

    public void register(String user,String pass,String role){
        UserEntity u=new UserEntity();
        u.setUsername(user);
        u.setPassword(encoder.encode(pass));
        u.setRole(role);
        repo.save(u);
    }

    public UserEntity login(String user,String pass){
        UserEntity u=repo.findByUsername(user).orElseThrow();
        if(!encoder.matches(pass,u.getPassword()))
            throw new RuntimeException("Invalid password");
        return u;
    }
}

