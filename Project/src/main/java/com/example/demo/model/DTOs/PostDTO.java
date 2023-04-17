package com.example.demo.model.DTOs;
import com.example.demo.model.entities.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private List<Tag> tags = new ArrayList<>();
    private List<CommentDTO> comments;
}
