package com.example.demo.model.repositories;

import com.example.demo.model.entities.CommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, CommentReaction.CommentReactionId> {

    Optional<CommentReaction> findByCommentIdAndUserId(Integer commentId, Integer userId);


}
