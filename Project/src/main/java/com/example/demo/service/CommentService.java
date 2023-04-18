package com.example.demo.service;


import com.example.demo.model.DTOs.CommentDTO;
import com.example.demo.model.DTOs.CommentReactionDTO;
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

import java.time.LocalDateTime;
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

        if (!optionalPost.isPresent()){
            throw new NotFoundException("The post you try to comment on is missing.");
        }
        if (content.length()>500){
            throw new BadRequestException("Can't exceed 500 chars limit.");
        }
        boolean hasParent = !parentId.equals("");
        Optional<Comment> optionalComment = null;

        if (hasParent){
            optionalComment=commentRepository.findById(Integer.parseInt(parentId));

            if (!optionalComment.isPresent()){
                throw new NotFoundException("The comment you try to reply to is missing.");
            }
            if (commentRepository.findByIdAndPostId(Integer.parseInt(parentId),postId).isEmpty()){
                throw new BadRequestException("The comment you are trying to reply to is not on the current post!");
            }
        }
        User user = userRepository.findById(userId).get();
        String url = saveFile(file);
        Comment comment = new Comment();
        comment.setCreatedAt(LocalDateTime.now());
        comment.setContent(content);
        if (hasParent){
            comment.setParent(optionalComment.get());
        }
        comment.setPost(optionalPost.get());
        comment.setOwner(user);
        comment.setFilePath(url);
        commentRepository.save(comment);
        return mapper.map(comment,CommentDTO.class);
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
    public CommentReactionDTO react(int id, int userId,boolean reaction){
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty()){
            throw new NotFoundException("The comment you are trying to react to is missing.");
        }
        Optional<CommentReaction> optionalCommentReaction = commentReactionRepository.findByIdCommentIdAndIdUserId(id,userId);

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

}
