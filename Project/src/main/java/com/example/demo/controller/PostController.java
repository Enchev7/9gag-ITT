package com.example.demo.controller;

import com.example.demo.model.DTOs.PostBasicInfoDTO;
import com.example.demo.model.DTOs.PostDTO;
import com.example.demo.service.PostService;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController extends AbstractController{
    
    @Autowired
    private PostService postService;
    
    @SneakyThrows
    @PostMapping("/posts")
    public PostDTO create(@RequestBody PostBasicInfoDTO dto, HttpSession s){
        Integer userId = (Integer) s.getAttribute("LOGGED_ID");
        if (userId == null){
            throw new UnavailableException("Log in to create new post");
        }
        return postService.create(dto, userId);
    }
    
}
