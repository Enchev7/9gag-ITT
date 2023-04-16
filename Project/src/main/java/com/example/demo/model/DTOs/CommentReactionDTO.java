package com.example.demo.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentReactionDTO {

    private UserWithoutPassDTO user;
    private CommentBasicInfoDTO comment;
    private boolean isLiked;


}
