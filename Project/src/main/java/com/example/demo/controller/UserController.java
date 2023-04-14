package com.example.demo.controller;

import com.example.demo.model.DTOs.LoginDTO;
import com.example.demo.model.DTOs.UserRegisterDataDTO;
import com.example.demo.model.DTOs.UserWithoutPassDTO;
import com.example.demo.model.entities.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/users/login")
    public UserWithoutPassDTO login(@RequestBody LoginDTO dto, HttpSession s){
        UserWithoutPassDTO respDto = userService.login(dto);
        s.setAttribute("LOGGED", true);
        s.setAttribute("LOGGED_ID", respDto.getId());
        return respDto;
    }
    @GetMapping("/users/logout")
    public ResponseEntity<String> logout(HttpSession session) {

        Boolean isLoggedIn = (Boolean) session.getAttribute("LOGGED");

        if (isLoggedIn != null && isLoggedIn) {
            session.invalidate();
            return ResponseEntity.ok("Logged out successfully");

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
    }



}
