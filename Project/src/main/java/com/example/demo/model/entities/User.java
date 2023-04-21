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
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
    @Column(name = "is_admin")
    private boolean isAdmin;
    @Column(name = "is_banned")
    private boolean isBanned;
//    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
//    private Set<Post> posts = new HashSet<>();
    @ManyToMany(mappedBy = "reportedBy", cascade = CascadeType.ALL)
    private Set<Post> reportedPosts = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
