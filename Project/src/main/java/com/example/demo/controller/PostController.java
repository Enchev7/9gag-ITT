package com.example.demo.controller;

import com.example.demo.model.DTOs.PostBasicInfoDTO;
import com.example.demo.model.DTOs.PostDTO;
import com.example.demo.model.DTOs.PostReactionDTO;
import com.example.demo.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController extends AbstractController{
    
    @Autowired
    private PostService postService;
    
    @SneakyThrows
    @PostMapping("/posts")
    public PostBasicInfoDTO create(@RequestBody PostDTO dto, HttpSession s){
        return postService.create(dto, getLoggedId(s));
    }
    @PutMapping("/posts/{id}/like_unlike")
    public PostReactionDTO likeUnlike(@PathVariable int id, HttpSession s){
        return postService.likeUnlike(id,getLoggedId(s));
    }
    @PutMapping("/posts/{id}/dislike_undislike")
    public PostReactionDTO dislikeUnDislike(@PathVariable int id, HttpSession s){
        return postService.dislikeUnDislike(id,getLoggedId(s));
    }
    @GetMapping("/posts/search")
    public List<PostBasicInfoDTO> search(@RequestParam("query") String query){
        return postService.search(query);
    }
    @GetMapping("/posts/sort_by_upload_date")
    public List<PostBasicInfoDTO> sortByUploadDate(){
        return postService.sortByUploadDate();
    }
    @DeleteMapping("/posts/{id}")
    public PostBasicInfoDTO delete(@PathVariable int id, HttpSession s){
        return postService.delete(id,getLoggedId(s));
    }


    
}
