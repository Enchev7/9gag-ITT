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
public class PostBasicInfoDTO {

    private int id;
    private String title;
    private String filePath;
    private UserWithoutPassDTO owner;
    private LocalDateTime createdAt = LocalDateTime.now();
}
