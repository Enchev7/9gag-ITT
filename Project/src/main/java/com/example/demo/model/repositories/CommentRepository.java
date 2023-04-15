package com.example.demo.model.repositories;

import com.example.demo.model.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentRepository extends JpaRepository<Comment,Integer> {
}
