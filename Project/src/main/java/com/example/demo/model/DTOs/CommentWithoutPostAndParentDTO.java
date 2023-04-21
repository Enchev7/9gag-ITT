package com.example.demo.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentWithoutPostAndParentDTO {

    private int id;
    private UserWithoutPassDTO owner;
    private String filePath;
    private LocalDateTime createdAt;
    private String content;
}
