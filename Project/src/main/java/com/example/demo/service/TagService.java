package com.example.demo.service;

import com.example.demo.model.entities.Tag;
import com.example.demo.model.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagService {
    
    @Autowired
    TagRepository tagRepository;
    
    public Tag findOrCreateTagByName(String name){
        Tag tag = tagRepository.findByName(name);
        if (tag != null){
            return tag;
        }
        else {
            Tag newTag = new Tag();
            newTag.setName(name);
            tagRepository.save(newTag);
            return newTag;
        }
    }

}
