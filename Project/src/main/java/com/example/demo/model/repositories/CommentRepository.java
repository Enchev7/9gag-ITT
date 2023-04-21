package com.example.demo.model.repositories;

import com.example.demo.model.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment,Integer> {

    Optional<Comment> findByIdAndPostId(Integer commentId, Integer postId);

    List<Comment> findAllByPostId(int postId);

}
