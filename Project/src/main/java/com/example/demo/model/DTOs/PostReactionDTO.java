package com.example.demo.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostReactionDTO {

    private UserWithoutPassDTO user;
    private PostBasicInfoDTO post;
    private boolean isLiked;
}
