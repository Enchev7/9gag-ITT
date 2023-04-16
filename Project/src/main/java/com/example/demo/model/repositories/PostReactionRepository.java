package com.example.demo.model.repositories;

import com.example.demo.model.entities.PostReaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, PostReaction.PostReactionId> {

    Optional<PostReaction> findByIdPostIdAndIdUserId(Integer postId, Integer userId);

}

