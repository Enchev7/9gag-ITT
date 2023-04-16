package com.example.demo.controller;

import com.example.demo.model.DTOs.PostBasicInfoDTO;
import com.example.demo.model.DTOs.PostDTO;
import com.example.demo.service.PostService;
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
    public PostBasicInfoDTO create(@RequestBody PostDTO dto, HttpSession s){
        Integer userId = (Integer) s.getAttribute("LOGGED_ID");
        return postService.create(dto, getLoggedId(s));
    }
}
