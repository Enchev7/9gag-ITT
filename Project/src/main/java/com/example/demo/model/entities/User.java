package com.example.demo.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "full_name")
    private String fullName;
    @Column
    private String email;
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    @Column
    private String password;
    @Column
    private int age;
    @Column(name = "ver_code")
    private String verCode;
    @Column(name = "is_verified")
    private boolean isVerified;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Post> posts=new ArrayList<>();
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Comment> comments=new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CommentReaction> commentReactions = new ArrayList<>();

}
