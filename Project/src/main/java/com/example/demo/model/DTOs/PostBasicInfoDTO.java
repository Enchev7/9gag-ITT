package com.example.demo.model.DTOs;

import com.example.demo.model.entities.Tag;
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
    private LocalDateTime createdAt = LocalDateTime.now();
}
