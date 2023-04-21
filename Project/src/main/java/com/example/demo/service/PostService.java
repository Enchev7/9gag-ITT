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
import jakarta.transaction.Transactional;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Transactional
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
        for (String tagName : tags) {
            Tag tag = tagService.findOrCreateTagByName(tagName);
            tag.getPostTags().add(post);
            post.getPostTags().add(tag);
        }
        postRepository.save(post);
        return mapper.map(post, PostBasicInfoDTO.class);
    }
    public PostReactionDTO react(int id, int userId,boolean reaction){

        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()){
            throw new NotFoundException("The post you are trying to react to is missing.");
        }
        Optional<PostReaction> optionalPostReaction = postReactionRepository.findByPostIdAndUserId(id,userId);

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

    /* 
    By default, if we do not pass parameters in the HTTP Request it shows all the results in one page.
    Using "?page=2&size=9" means that it will return the second page and 9 results for each page.
     */
    public Page<PostBasicInfoDTO> search(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "9") int size, String query) {
        Set<Post> posts = new HashSet<>();
        posts.addAll(postRepository.findByTitleContainingIgnoreCase(query));
        posts.addAll(postRepository.findByTagNameContainingIgnoreCase("%" + query + "%"));
        List<Post> postList = new ArrayList<>(posts);
        int start = page * size;
        int end = Math.min(start + size, postList.size());
        Page<Post> postPage = new PageImpl<>(postList.subList(start, end), PageRequest.of(page, size), postList.size());
        return postPage.map(post -> mapper.map(post, PostBasicInfoDTO.class));
    }
    
    public Page<PostBasicInfoDTO> getTrending(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.sortedByTrending(LocalDate.now().minusDays(10).atStartOfDay().withHour(0).withMinute(0).withSecond(0), pageable);
        return posts.map(p -> mapper.map(p, PostBasicInfoDTO.class));
    }

    /* 
    By default, if we do not pass parameters in the HTTP Request it shows all the results in one page.
    Using "?page=2&size=9" means that it will return the second page and 9 results for each page.
     */
    public Page<PostBasicInfoDTO> fresh(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.fresh(LocalDateTime.now().with(LocalTime.MIN), pageable);

        return posts.map(post -> mapper.map(post, PostBasicInfoDTO.class));
    }

    public Page<PostBasicInfoDTO> getTop(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.sortedByTop(pageable);
        return posts.map(post -> mapper.map(post, PostBasicInfoDTO.class));
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
            String filePath = optionalPost.get().getFilePath();
            if (filePath != null && Files.exists(Path.of(filePath))){
                Files.delete(Path.of(optionalPost.get().getFilePath()));
            }
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
        post.setReports(post.getReports()+1);
        optionalUser.get().getReportedPosts().add(post);
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
