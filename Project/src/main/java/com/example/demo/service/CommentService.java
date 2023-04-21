package com.example.demo.service;



import com.example.demo.model.DTOs.CommentDTO;
import com.example.demo.model.DTOs.CommentReactionDTO;
import com.example.demo.model.DTOs.CommentWithoutPostAndParentDTO;
import com.example.demo.model.entities.*;
import com.example.demo.model.exceptions.BadRequestException;
import com.example.demo.model.exceptions.NotFoundException;
import com.example.demo.model.exceptions.UnauthorizedException;
import com.example.demo.model.repositories.CommentReactionRepository;
import com.example.demo.model.repositories.CommentRepository;
import com.example.demo.model.repositories.PostRepository;
import com.example.demo.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService extends AbstractService{
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentReactionRepository commentReactionRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper mapper;


    public CommentDTO create(int postId, MultipartFile file,String content,String parentId,int userId) {
        Optional<Post> optionalPost = postRepository.findById(postId);

        if (optionalPost.isEmpty()){
            throw new NotFoundException("The post you try to comment on is missing.");
        }
        if (content.length()>500){
            throw new BadRequestException("Can't exceed 500 chars limit.");
        }
        if (content.isEmpty() && file.isEmpty()){
            throw new BadRequestException("Can't create a comment without any content.");
        }
        boolean hasParent = !parentId.equals("");
        Optional<Comment> optionalComment = null;

        if (hasParent){
            optionalComment=commentRepository.findById(Integer.parseInt(parentId));

            if (optionalComment.isEmpty()){
                throw new NotFoundException("The comment you try to reply to is missing.");
            }
            if (commentRepository.findByIdAndPostId(Integer.parseInt(parentId),postId).isEmpty()){
                throw new BadRequestException("The comment you are trying to reply to is not on the current post!");
            }
        }
        User user = userRepository.findById(userId).get();

        String url = null;
        if (!file.isEmpty()){
            url = saveFile(file);
        }
        Comment comment = new Comment();
        comment.setCreatedAt(LocalDateTime.now());
        comment.setContent(content);
        comment.setPost(optionalPost.get());
        comment.setOwner(user);
        comment.setFilePath(url);
        if (hasParent){
            comment.setParent(optionalComment.get());
        }
        optionalPost.get().getComments().add(comment);
        commentRepository.save(comment);
        return mapper.map(comment,CommentDTO.class);
    }
    public CommentDTO delete(int id,int userId){

        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty()){
            throw new NotFoundException("Comment not found!");
        }
        User owner = optionalComment.get().getOwner();
        Optional<User> admin = userRepository.findById(userId);
        if (owner.getId()!=userId && !admin.get().isAdmin()){
            throw new UnauthorizedException("Can't delete a comment you haven't created yourself!");
        }
        try {
            String filePath = optionalComment.get().getFilePath();
            if (filePath != null && Files.exists(Path.of(filePath))){
                Files.delete(Path.of(optionalComment.get().getFilePath()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        commentRepository.delete(optionalComment.get());
        return mapper.map(optionalComment.get(),CommentDTO.class);
    }
    public CommentReactionDTO react(int id, int userId,boolean reaction){

        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty()){
            throw new NotFoundException("The comment you are trying to react to is missing.");
        }
        Optional<CommentReaction> optionalCommentReaction = commentReactionRepository.findByCommentIdAndUserId(id,userId);

        CommentReaction commentReaction;
        if (optionalCommentReaction.isPresent()){
            commentReaction=optionalCommentReaction.get();
            if (commentReaction.isLiked() == reaction){
                commentReactionRepository.delete(commentReaction);
            }
            else {
                commentReaction.setLiked(reaction);
                commentReactionRepository.save(commentReaction);
            }
        }
        else {
            commentReaction=new CommentReaction();
            commentReaction.setId(new CommentReaction.CommentReactionId(userId, id));
            commentReaction.setUser(userRepository.findById(userId).get());
            commentReaction.setComment(optionalComment.get());
            commentReaction.setLiked(reaction);
            commentReactionRepository.save(commentReaction);
        }
        return mapper.map(commentReaction,CommentReactionDTO.class);
    }

    public List<CommentWithoutPostAndParentDTO> viewComments(int postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()){
            throw new NotFoundException("Post not found.");
        }
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        List<CommentWithoutPostAndParentDTO> commentDTOS=new ArrayList<>();
        for (Comment c:comments){
            commentDTOS.add(mapper.map(c,CommentWithoutPostAndParentDTO.class));
        }
        return commentDTOS;
    }
}
