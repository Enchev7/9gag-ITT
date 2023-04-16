package com.example.demo.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parent;
    @Column(name = "file_path")
    private String filePath;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column
    private String content;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> replies =new ArrayList<>();
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentReaction> commentReactions = new ArrayList<>();


}
