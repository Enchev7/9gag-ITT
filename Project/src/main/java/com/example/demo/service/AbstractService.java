package com.example.demo.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public abstract class AbstractService {
    protected int pageSize = 9;
    protected String saveFile(MultipartFile file){
        try {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + "."+ext;
            File dir = new File("uploads");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File f = new File(dir, fileName);
            Files.copy(file.getInputStream(), f.toPath());
            String url = dir.getName() + File.separator + f.getName();
            return url;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
