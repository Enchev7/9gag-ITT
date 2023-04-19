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
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class PostService extends AbstractService{
    
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
        if(title == null || title.length() == 0 || title.length() > 280) {
            throw new BadRequestException("Title should be a string up to 280 symbols");
        }
        if(file == null || file.isEmpty() || !isValidFileType(file)) {
            throw new BadRequestException("File type not supported. Please upload an image (jpeg, jpg, png, gif) or a video (mp4, webm, quicktime, x-m4v)");
        }
        User u = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Post post = new Post();
        post.setTitle(title);
        post.setOwner(u);
        post.setCreatedAt(LocalDateTime.now());
        post.setPostTags(new HashSet<>());
        
        String url = saveFile(file);
        post.setFilePath(url);
        for (String tag : tags) {
            post.getPostTags().add(tagService.findOrCreateTagByName(tag));
        }
        postRepository.save(post);
        return mapper.map(post, PostBasicInfoDTO.class);
    }
    public PostReactionDTO react(int id, int userId,boolean reaction){
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()){
            throw new NotFoundException("The post you are trying to react to is missing.");
        }
        Optional<PostReaction> optionalPostReaction = postReactionRepository.findByIdPostIdAndIdUserId(id,userId);

        PostReaction postReaction;
        if (optionalPostReaction.isPresent()){
            postReaction=optionalPostReaction.get();
            if (postReaction.isLiked() == reaction){
                postReactionRepository.delete(postReaction);
            }
            else{
                postReaction.setLiked(reaction);
                postReactionRepository.save(postReaction);
            }
        }
        else {
            postReaction=new PostReaction();
            postReaction.setId(new PostReaction.PostReactionId(userId, id));
            postReaction.setUser(userRepository.findById(userId).get());
            postReaction.setPost(optionalPost.get());
            postReaction.setLiked(reaction);
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

    public List<PostBasicInfoDTO> getTrending() {
        List<Post> posts = new ArrayList<>();
        posts.addAll(postRepository.sortedByTrending(LocalDate.now().minusDays(10).atStartOfDay().withHour(0).withMinute(0).withSecond(0)));
        List<PostBasicInfoDTO> postsDTOs = new ArrayList<>();

        for (Post p:posts){
            postsDTOs.add(mapper.map(p, PostBasicInfoDTO.class));
        }
        return postsDTOs;
    }

    public List<PostBasicInfoDTO> fresh() {
        List<Post> posts = new ArrayList<>();
        posts.addAll(postRepository.fresh(LocalDateTime.now().with(LocalTime.MIN)));
        List<PostBasicInfoDTO> postsDTOs = new ArrayList<>();

        for (Post p:posts){
            postsDTOs.add(mapper.map(p, PostBasicInfoDTO.class));
        }
        return postsDTOs;
    }
    public List<PostBasicInfoDTO> getTop() {
        List<Post> posts = new ArrayList<>();
        posts.addAll(postRepository.sortedByTop());
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
        User owner = optionalPost.get().getOwner();
        Optional<User> admin = userRepository.findById(userId);
        if (owner.getId()!=userId && !admin.get().isAdmin()){
            throw new UnauthorizedException("Can't delete a post you haven't created yourself!");
        }
        try {
            Files.delete(Path.of(optionalPost.get().getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        postRepository.delete(optionalPost.get());
        return mapper.map(optionalPost.get(),PostBasicInfoDTO.class);
    }
    public Resource downloadMedia(int postId) {
        Post post = findById(postId);
        File file = getMediaFile(post);
        Resource resource = new FileSystemResource(file);
        return resource;
    }

    public PostBasicInfoDTO report(int postId, int userId){
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()){
            throw new NotFoundException("Post not found!");
        }
        Post post = optionalPost.get();
        if (post.getReports()>5){
            throw new NotFoundException("Post not found!");
        }
        Optional<User> optionalUser = userRepository.findById(userId);

        if (post.getReportedBy().size()>0){
            boolean hasReported=false;
            for (User u:post.getReportedBy()){
                if (u.getId()==optionalUser.get().getId()){
                    hasReported=true;
                    break;
                }
            }
            if (hasReported){
                throw new BadRequestException("You've already reported this post.");
            }
        }
        post.getReportedBy().add(optionalUser.get());
        optionalUser.get().getReportedPosts().add(post);
        post.setReports(post.getReports()+1);
        postRepository.save(post);
        return mapper.map(post,PostBasicInfoDTO.class);
    }
    private boolean isValidFileType(MultipartFile file) {
        List<String> validFileTypes = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif", "video/mp4", "video/webm", "video/quicktime", "video/x-m4v");
        String fileType = file.getContentType();
        return validFileTypes.contains(fileType);
    }

    private Post findById(int id) {
        return postRepository.findById(id).orElseThrow(() -> new  NotFoundException("Post not found"));
    }

    public String getMediaType(int postId) {
        Post post = findById(postId);
        String extension = FilenameUtils.getExtension(post.getFilePath());
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "mp4":
                return "video/mp4";
            case "webm":
                return "video/webm";
            case "mov":
                return "video/quicktime";
            case "m4v":
                return "video/x-m4v";
            default:
                throw new BadRequestException("Unsupported media type");
        }
    }

    private File getMediaFile(Post post) {
        File file = new File(post.getFilePath());
        if (!file.exists()) {
            throw new NotFoundException("File not found");
        }
        return file;
    }
}
