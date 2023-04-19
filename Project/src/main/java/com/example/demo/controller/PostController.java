package com.example.demo.controller;

import com.example.demo.model.DTOs.PostBasicInfoDTO;
import com.example.demo.model.DTOs.PostReactionDTO;
import com.example.demo.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class PostController extends AbstractController{
    
    @Autowired
    private PostService postService;
    
    @SneakyThrows
    @PostMapping("/posts")
    public PostBasicInfoDTO create(@RequestParam("title")String title, 
                                   @RequestParam("file")MultipartFile file, 
                                   @RequestParam("tags")String[] tags, HttpSession s){
        return postService.create(title, file, tags, getLoggedId(s));
    }
    @PutMapping("/posts/{id}/like_unlike")
    public PostReactionDTO likeUnlike(@PathVariable int id, HttpSession s){
        return postService.react(id, getLoggedId(s),true);
    }
    @PutMapping("/posts/{id}/dislike_undislike")
    public PostReactionDTO dislikeUnDislike(@PathVariable int id, HttpSession s){
        return postService.react(id,getLoggedId(s),false);
    }
    @GetMapping("/posts/search")
    public List<PostBasicInfoDTO> search(@RequestParam("query") String query){
        return postService.search(query);
    }
    @GetMapping("/posts/fresh")
    public List<PostBasicInfoDTO> fresh(){
        return postService.fresh();
    }
    @GetMapping("/posts/trending")
    public List<PostBasicInfoDTO> trending(){
        return postService.getTrending();
    }

    @GetMapping("/posts/top")
    public List<PostBasicInfoDTO> top(){
        return postService.getTop();
    }
    
    
    @DeleteMapping("/posts/{id}")
    public PostBasicInfoDTO delete(@PathVariable int id, HttpSession s){
        return postService.delete(id,getLoggedId(s));
    }

    @SneakyThrows
    @GetMapping("posts/{id}/download_media")
    public ResponseEntity<Resource> download(@PathVariable("id") int postId, HttpSession s) {
        int userId = getLoggedId(s);
        Resource resource = postService.downloadMedia(postId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType(postService.getMediaType(postId)))
                .body(resource);
    }
    @PutMapping("/posts/{id}/report")
    public PostBasicInfoDTO report(@PathVariable int id,HttpSession s){
        return postService.report(id,getLoggedId(s));
    }
}
