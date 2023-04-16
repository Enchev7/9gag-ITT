package com.example.demo.service;

import com.example.demo.model.DTOs.PostBasicInfoDTO;
import com.example.demo.model.DTOs.PostDTO;
import com.example.demo.model.DTOs.PostReactionDTO;
import com.example.demo.model.entities.*;
import com.example.demo.model.exceptions.NotFoundException;
import com.example.demo.model.repositories.PostReactionRepository;
import com.example.demo.model.repositories.PostRepository;
import com.example.demo.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService {
    
    @Autowired
    private ModelMapper mapper;
    
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostReactionRepository postReactionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public PostDTO create(PostBasicInfoDTO dto, Integer userId){
        //TODO
        //validate post info
        User u = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Post post = mapper.map(dto, Post.class);
        post.setOwner(u);
        postRepository.save(post);
        return mapper.map(post, PostDTO.class);
    }






























































    public PostReactionDTO likeUnlike(int id, int userId){
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()){
            throw new NotFoundException("The post you are trying to react to is missing.");
        }
        Optional<PostReaction> optionalPostReaction = postReactionRepository.findByIdPostIdAndIdUserId(id,userId);

        PostReaction postReaction;
        if (optionalPostReaction.isPresent()){
            postReaction=optionalPostReaction.get();
            postReactionRepository.delete(postReaction);
        }
        else {
            postReaction=new PostReaction();
            postReaction.setId(new PostReaction.PostReactionId(userId, id));
            postReaction.setUser(userRepository.findById(userId).get());
            postReaction.setPost(optionalPost.get());
            postReaction.setLiked(true);
            postReactionRepository.save(postReaction);
        }
        return mapper.map(postReaction,PostReactionDTO.class);
    }

    public PostReactionDTO dislikeUnDislike(int id, int userId){
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()){
            throw new NotFoundException("The post you are trying to react to is missing.");
        }
        Optional<PostReaction> optionalPostReaction = postReactionRepository.findByIdPostIdAndIdUserId(id,userId);

        PostReaction postReaction;
        if (optionalPostReaction.isPresent()){
            postReaction=optionalPostReaction.get();
            postReactionRepository.delete(postReaction);
        }
        else {
            postReaction=new PostReaction();
            postReaction.setId(new PostReaction.PostReactionId(userId, id));
            postReaction.setUser(userRepository.findById(userId).get());
            postReaction.setPost(optionalPost.get());
            postReaction.setLiked(false);
            postReactionRepository.save(postReaction);
        }
        return mapper.map(postReaction,PostReactionDTO.class);
    }

}
