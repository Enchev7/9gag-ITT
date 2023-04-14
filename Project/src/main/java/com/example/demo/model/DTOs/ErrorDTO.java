package com.example.demo.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
public class ErrorDTO extends AbstractDTO{

    private String msg;
    private int status;
    private LocalDateTime time;
}
