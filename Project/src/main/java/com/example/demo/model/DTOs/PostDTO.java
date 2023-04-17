package com.example.demo.model.DTOs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    
    private int id;
    private String title;
    private String filePath;
    private UserWithoutPassDTO owner;
    private LocalDateTime createdAt;
    private Set<TagDTO> tags = new HashSet<>();
    private Set<CommentDTO> comments=new HashSet<>();
}
