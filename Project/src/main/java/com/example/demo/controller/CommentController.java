package com.example.demo.controller;

import com.example.demo.model.DTOs.CommentDTO;
import com.example.demo.model.DTOs.CommentReactionDTO;
import com.example.demo.model.exceptions.UnauthorizedException;
import com.example.demo.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class CommentController extends AbstractController{

    @Autowired
    private CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public CommentDTO create(@PathVariable int postId, @RequestParam("file")MultipartFile file,
                             @RequestParam("content")String content, @RequestParam("parentId")String parentId,
                             HttpSession s){

        Integer userId = (Integer) s.getAttribute("LOGGED_ID");
        if (userId == null){
            throw new UnauthorizedException("Log in first.");
        }
        return commentService.create(postId,file,content,parentId,userId);
    }
    @DeleteMapping("/comments/{id}")
    public CommentDTO delete(@PathVariable int id,HttpSession s){

        Integer userId = (Integer) s.getAttribute("LOGGED_ID");
        if (userId == null){
            throw new UnauthorizedException("Log in first.");
        }
        return commentService.delete(id,userId);
    }

    @PutMapping("/comments/{id}/like_unlike")
    public CommentReactionDTO likeUnlike(@PathVariable int id, HttpSession s){
        Integer userId = (Integer) s.getAttribute("LOGGED_ID");
        if (userId == null){
            throw new UnauthorizedException("Log in first.");
        }
        return commentService.likeUnlike(id,userId);
    }
}
