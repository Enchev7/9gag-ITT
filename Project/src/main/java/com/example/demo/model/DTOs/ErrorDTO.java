package com.example.demo.model.DTOs;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDTO {

    private String msg;
    private int status;
    private LocalDateTime time;
}
