package com.example.demo.service;


import com.example.demo.model.DTOs.CommentDTO;
import com.example.demo.model.entities.Comment;
import com.example.demo.model.entities.Post;
import com.example.demo.model.entities.User;
import com.example.demo.model.exceptions.BadRequestException;
import com.example.demo.model.exceptions.NotFoundException;
import com.example.demo.model.exceptions.UnauthorizedException;
import com.example.demo.model.repositories.CommentRepository;
import com.example.demo.model.repositories.PostRepository;
import com.example.demo.model.repositories.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper mapper;

    public CommentDTO create(int postId, MultipartFile file,String content,String parentId,int userId) {
        Optional<Post> optionalPost = postRepository.findById(postId);

        if (!optionalPost.isPresent()){
            throw new NotFoundException("The post you try to comment on is missing.");
        }
        if (content.length()>500){
            throw new BadRequestException("Can't exceed 500 chars limit.");
        }
        boolean hasParent = parentId.length() > 0;
        Optional<Comment> optionalComment = null;

        if (hasParent){
            optionalComment=commentRepository.findById(Integer.parseInt(parentId));

            if (!optionalComment.isPresent()){
                throw new NotFoundException("The comment you try to reply to is missing.");
            }
        }
        Optional<User> optionalUser = userRepository.findById(userId);

        try{
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String name = UUID.randomUUID() + "."+ext;
            File dir = new File("C:\\Encho\\ITT\\uploads");
            if(!dir.exists()){
                dir.mkdirs();
            }
            File f = new File(dir, name);
            Files.copy(file.getInputStream(), f.toPath());
            String url = dir.getName() + File.separator + f.getName();

            Comment comment = new Comment();
            comment.setCreatedAt(LocalDateTime.now());
            comment.setContent(content);
            if (hasParent){
                comment.setParent(optionalComment.get());
            }
            comment.setPost(optionalPost.get());
            comment.setOwner(optionalUser.get());
            comment.setFilePath(url);
            commentRepository.save(comment);
            return mapper.map(comment,CommentDTO.class);
        }
        catch (IOException e){
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }
    public CommentDTO delete(int id,int userId){

        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty()){
            throw new NotFoundException("Comment not found!");
        }
        if (optionalComment.get().getOwner().getId()!=userId){
            throw new UnauthorizedException("Can't delete a comment you haven't created yourself!");
        }
        commentRepository.delete(optionalComment.get());
        return mapper.map(optionalComment.get(),CommentDTO.class);
    }
}
