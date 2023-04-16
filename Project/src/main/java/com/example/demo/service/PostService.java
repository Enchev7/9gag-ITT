package com.example.demo.service;

import com.example.demo.model.DTOs.PostDTO;
import com.example.demo.model.DTOs.PostBasicInfoDTO;
import com.example.demo.model.DTOs.PostDTO;
import com.example.demo.model.entities.Post;
import com.example.demo.model.entities.Tag;
import com.example.demo.model.entities.User;
import com.example.demo.model.exceptions.NotFoundException;
import com.example.demo.model.repositories.PostRepository;
import com.example.demo.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
public class PostService {
    
    @Autowired
    private ModelMapper mapper;
    
    @Autowired
    private TagService tagService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    public PostBasicInfoDTO create(PostDTO dto, Integer userId){
        //TODO
        //validate post info
        User u = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Post post = mapper.map(dto, Post.class);
        post.setOwner(u);
        post.setCreatedAt(LocalDateTime.now());
        post.setPostTags(new HashSet<>());
        for (Tag tag : dto.getTags()){
            post.getPostTags().add(tagService.findOrCreateTagByName(tag.getName()));
        }
        postRepository.save(post);
        return mapper.map(post, PostBasicInfoDTO.class);
    }
}
