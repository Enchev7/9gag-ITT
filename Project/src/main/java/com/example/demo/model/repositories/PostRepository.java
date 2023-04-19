package com.example.demo.model.repositories;

import com.example.demo.model.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findByTitleContainingIgnoreCase(String title);
    @Query("SELECT p FROM posts p JOIN p.postTags t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :searchString, '%'))")
    List<Post> findByTagNameContainingIgnoreCase(String searchString);
    List<Post> findAllByOrderByCreatedAtDesc();
    

    @Query("SELECT p FROM posts p " +
       "JOIN p.comments c " +
       "JOIN p.postReactions pr " + 
       "WHERE p.createdAt >= :date " +
       "GROUP BY p.id " +
       "ORDER BY COUNT(c.id) DESC, COUNT(pr.post.id) DESC")
    List<Post> sortedByTrending(@Param("date") LocalDateTime date);

    @Query("SELECT p FROM posts p " +
            "JOIN p.comments c " +
            "JOIN p.postReactions pr " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c.id) DESC, COUNT(pr.post.id) DESC")
    List<Post> sortedByTop();
    
    @Query("SELECT p FROM posts p WHERE p.createdAt >= :startOfDay")
    List<Post> fresh(@Param("startOfDay") LocalDateTime startOfDay);
}
