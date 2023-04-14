package com.example.demo.model.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String title;
    @Column(name = "file_path")
    private String filePath;
    @Column(name = "user_id")
    private int user;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
}
