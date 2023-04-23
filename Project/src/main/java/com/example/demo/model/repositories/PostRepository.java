package com.example.demo.model.repositories;

import com.example.demo.model.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findByTitleContainingIgnoreCase(String title);
    @Query(value = "SELECT * FROM posts p\n" +
            "WHERE LOWER (p.title) LIKE lower(?)", nativeQuery = true)  
    List<Post> findByTagNameContainingIgnoreCase(String searchString);


    @Query(value = "SELECT p.id, p.title, p.file_path, p.user_id, p.created_at, p.reports," +
            "            (SELECT COUNT(c.id) FROM comments c WHERE c.post_id = p.id) + " +
            "           (SELECT COUNT(pr.post_id) FROM post_reactions pr WHERE pr.post_id = p.id AND pr.is_liked = 1) AS total_count" +
            "            FROM posts p ORDER BY total_count DESC",
            countQuery = "SELECT COUNT(*) FROM posts WHERE created_at >= DATE_SUB(NOW(), INTERVAL 1 DAY)",
            nativeQuery = true)
    Page<Post> sortedByTrending(Pageable pageable);


    @Query(value = "SELECT p.id, p.title, p.file_path, p.user_id, p.created_at, p.reports, \n" +
            "(SELECT COUNT(c.id) FROM comments c WHERE c.post_id = p.id) + \n" +
            "(SELECT COUNT(pr.post_id) FROM post_reactions pr WHERE pr.post_id = p.id AND pr.is_liked = 1) AS total_count\n" +
            "FROM posts p ORDER BY total_count DESC", countQuery = "SELECT COUNT(*) FROM posts", nativeQuery = true)
    Page<Post> sortedByTop(Pageable pageable);

    @Query("SELECT p FROM posts p WHERE p.createdAt >= :startOfDay")
    Page<Post> fresh(@Param("startOfDay") LocalDateTime startOfDay, Pageable pageable);



}
