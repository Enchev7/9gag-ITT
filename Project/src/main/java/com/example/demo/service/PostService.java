package com.example.demo.service;

import com.example.demo.model.DTOs.PostDTO;
import com.example.demo.model.DTOs.PostInfoDTO;
import com.example.demo.model.repositories.PostRepository;
import com.example.demo.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    
    @Autowired
    private ModelMapper mapper;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public PostInfoDTO create(PostDTO dto, Integer userId){
        return null;
    }
}
