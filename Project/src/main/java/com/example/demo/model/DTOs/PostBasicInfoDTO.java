package com.example.demo.model.DTOs;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostBasicInfoDTO {

    private int id;
    private String title;
    private String filePath;
    private LocalDateTime createdAt;
    private Set<TagDTO> tags=new HashSet<>();
    private int reports;
    private Set<UserWithoutPassDTO> reportedBy=new HashSet<>();



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostBasicInfoDTO that = (PostBasicInfoDTO) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
