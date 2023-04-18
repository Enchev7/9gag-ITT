package com.example.demo.controller;

import com.example.demo.model.DTOs.CommentDTO;
import com.example.demo.model.DTOs.CommentReactionDTO;
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
        return commentService.create(postId,file,content,parentId,getLoggedId(s));
    }
    @DeleteMapping("/comments/{id}")
    public CommentDTO delete(@PathVariable int id,HttpSession s){
        return commentService.delete(id,getLoggedId(s));
    }

    @PutMapping("/comments/{id}/like_unlike")
    public CommentReactionDTO likeUnlike(@PathVariable int id, HttpSession s){
        return commentService.likeUnlike(id,getLoggedId(s));
    }
    @PutMapping("/comments/{id}/dislike_undislike")
    public CommentReactionDTO dislikeUnDislike(@PathVariable int id, HttpSession s){
        return commentService.dislikeUnDislike(id,getLoggedId(s));
    }
}
