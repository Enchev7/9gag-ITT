package com.example.demo.controller;

import com.example.demo.model.DTOs.UserRegisterDataDTO;
import com.example.demo.model.DTOs.UserWithoutPassDTO;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController extends AbstractController{
    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public UserWithoutPassDTO register(@RequestBody UserRegisterDataDTO registerData){
        return userService.register(registerData);
    }
    @GetMapping("users/verify")
    public UserWithoutPassDTO verify(@RequestParam("code") String code){
        return userService.verify(code);
    }

}
