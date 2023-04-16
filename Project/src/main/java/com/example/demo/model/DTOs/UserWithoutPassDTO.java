package com.example.demo.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserWithoutPassDTO {

    private int id;
    private String email;
    private int age;
    private boolean isVerified;
//    private List<PostBasicInfoDTO> posts;
//    private List<CommentBasicInfoDTO> comments;
//   private List<CommentReactionDTO> commentsReactions;




}
