package com.project.controller;

// In imports ko add kiya gaya hai taaki compilation error na aaye
import com.project.service.MediaService; 
import com.project.model.Media;           
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getMediaById(@PathVariable Long id) {
        // Branch 1: Check if ID is valid
        if (id <= 0) {
            return ResponseEntity.badRequest().body("Invalid ID");
        }

        try {
            Media data = mediaService.findById(id);
            // Branch 2: Check if data exists
            if (data == null) {
                return ResponseEntity.status(404).body("Not Found");
            }
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            // Branch 3: Catch block coverage
            return ResponseEntity.status(500).body("Server Error");
        }
    }
}