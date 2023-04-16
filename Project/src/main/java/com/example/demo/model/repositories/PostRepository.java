package com.example.demo.model.repositories;

import com.example.demo.model.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT p FROM posts p JOIN p.postTags t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :searchString, '%'))")
    List<Post> findByTagNameContainingIgnoreCase(String searchString);
}
