package com.example.demo.service;

import com.example.demo.model.DTOs.PostBasicInfoDTO;
import com.example.demo.model.entities.Post;
import com.example.demo.model.entities.User;
import com.example.demo.model.DTOs.PostReactionDTO;
import com.example.demo.model.entities.*;
import com.example.demo.model.exceptions.BadRequestException;
import com.example.demo.model.exceptions.NotFoundException;
import com.example.demo.model.exceptions.UnauthorizedException;
import com.example.demo.model.repositories.PostReactionRepository;
import com.example.demo.model.repositories.PostRepository;
import com.example.demo.model.repositories.UserRepository;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

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
    
    public PostBasicInfoDTO create(String title, MultipartFile file, String[] tags, Integer userId){
        //TODO
        //validate post info
        User u = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Post post = new Post();
        post.setTitle(title);
        post.setOwner(u);
        post.setCreatedAt(LocalDateTime.now());
        post.setPostTags(new HashSet<>());
        try {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + "." + ext;
            File dir = new File("uploads");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File f = new File(dir, fileName);
//            f.createNewFile();
            Files.copy(file.getInputStream(), f.toPath());
            String url = dir.getName() + File.separator + f.getName();
            post.setFilePath(url);
            for (String tag : tags) {
                post.getPostTags().add(tagService.findOrCreateTagByName(tag));
            }
            postRepository.save(post);
            return mapper.map(post, PostBasicInfoDTO.class);
        }
        catch (IOException e){
            e.printStackTrace();
            throw new BadRequestException(e.getMessage() + "File save unsuccessful");
        }
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
