package com.example.demo.service;

import com.example.demo.model.DTOs.PostDTO;
import com.example.demo.model.DTOs.PostBasicInfoDTO;
import com.example.demo.model.DTOs.TagDTO;
import com.example.demo.model.entities.Post;
import com.example.demo.model.entities.User;
import com.example.demo.model.DTOs.PostReactionDTO;
import com.example.demo.model.entities.*;
import com.example.demo.model.exceptions.NotFoundException;
import com.example.demo.model.exceptions.UnauthorizedException;
import com.example.demo.model.repositories.PostReactionRepository;
import com.example.demo.model.repositories.PostRepository;
import com.example.demo.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    
    @Autowired
    private ModelMapper mapper;
    
    @Autowired
    private TagService tagService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostReactionRepository postReactionRepository;
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
        for (TagDTO tagDto : dto.getTags()){
            post.getPostTags().add(tagService.findOrCreateTagByName(tagDto.getName()));
        }
        postRepository.save(post);
        return mapper.map(post, PostBasicInfoDTO.class);
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
    public List<PostBasicInfoDTO> search(String query){
        List<Post> posts = new ArrayList<>();
        posts.addAll(postRepository.findByTitleContainingIgnoreCase(query));
        posts.addAll(postRepository.findByTagNameContainingIgnoreCase(query));
        List<PostBasicInfoDTO> postsDTOs = new ArrayList<>();

        for (Post p:posts){
            postsDTOs.add(mapper.map(p, PostBasicInfoDTO.class));
        }
        return postsDTOs;
    }

    public List<PostBasicInfoDTO> sortByUploadDate() {
        List<Post> posts = new ArrayList<>();
        posts.addAll(postRepository.findAllByOrderByCreatedAtDesc());
        List<PostBasicInfoDTO> postsDTOs = new ArrayList<>();

        for (Post p:posts){
            postsDTOs.add(mapper.map(p, PostBasicInfoDTO.class));
        }
        return postsDTOs;
    }

    public PostBasicInfoDTO delete(int id, int userId){

        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()){
            throw new NotFoundException("Post not found!");
        }
        if (optionalPost.get().getOwner().getId()!=userId){
            throw new UnauthorizedException("Can't delete a post you haven't created yourself!");
        }
        postRepository.delete(optionalPost.get());
        return mapper.map(optionalPost.get(),PostBasicInfoDTO.class);
    }
}
