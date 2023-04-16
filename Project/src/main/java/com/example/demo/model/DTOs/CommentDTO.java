package com.example.demo.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private int id;
    private UserWithoutPassDTO owner;
    private PostBasicInfoDTO post;
    private CommentBasicInfoDTO parent;
    private List<CommentReactionDTO> reactions;
    private String filePath;
    private LocalDateTime createdAt;
    private String content;
}