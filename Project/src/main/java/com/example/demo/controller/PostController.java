package com.example.demo.controller;

import com.example.demo.model.DTOs.PostBasicInfoDTO;
import com.example.demo.model.DTOs.PostDTO;
import com.example.demo.model.DTOs.PostReactionDTO;
import com.example.demo.model.exceptions.UnauthorizedException;
import com.example.demo.service.PostService;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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






































































    @PutMapping("/posts/{id}/like_unlike")
    public PostReactionDTO likeUnlike(@PathVariable int id, HttpSession s){
        Integer userId = (Integer) s.getAttribute("LOGGED_ID");
        if (userId == null){
            throw new UnauthorizedException("Log in first.");
        }
        return postService.likeUnlike(id,userId);
    }
    @PutMapping("/posts/{id}/dislike_undislike")
    public PostReactionDTO dislikeUnDislike(@PathVariable int id, HttpSession s){
        Integer userId = (Integer) s.getAttribute("LOGGED_ID");
        if (userId == null){
            throw new UnauthorizedException("Log in first.");
        }
        return postService.dislikeUnDislike(id,userId);
    }


    
}
