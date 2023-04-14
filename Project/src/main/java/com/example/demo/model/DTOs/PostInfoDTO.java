package com.example.demo.model.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PostInfoDTO {
    
    private int id;
    private String title;
    private String filePath;
    private UserWithoutPassDTO owner;
    private LocalDateTime createdAt;
}
