package com.example.demo.service;

import com.example.demo.model.DTOs.UserRegisterDataDTO;
import com.example.demo.model.DTOs.UserWithoutPassDTO;
import com.example.demo.model.entities.User;
import com.example.demo.model.exceptions.BadRequestException;
import com.example.demo.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserWithoutPassDTO register(UserRegisterDataDTO registerData){
        if (!registerData.getFullName().matches("^[A-Z][a-z]+(\\s[A-Z][a-z]+)?$")){
            throw new BadRequestException("Invalid full name!");
        }
        if (userRepository.existsByEmail(registerData.getEmail())){
            throw new BadRequestException("Email already exists!");
        }
        if (!registerData.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")){
            throw new BadRequestException("Invalid email!");
        }
        if (registerData.getPassword().length()<6){
            throw new BadRequestException("Pass should be at least 6 chars long.");
        }
        if (!String.valueOf(registerData.getAge()).matches("^([1-9]|[1-9][0-9]|1[0-4][0-9]|150)$")){
            throw new BadRequestException("Invalid age!");
        }

        User u = mapper.map(registerData,User.class);
        u.setLastLoginTime(LocalDateTime.now());
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        userRepository.save(u);
        return mapper.map(u,UserWithoutPassDTO.class);
    }
}
