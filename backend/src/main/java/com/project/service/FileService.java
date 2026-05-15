package com.project.service;

import com.project.model.MediaFile;
import com.project.repository.MediaFileRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private MediaFileRepository repository;

    private final String uploadDir = "uploads/";

    /**
     * File ko local storage aur DB mein save karta hai [cite: 5, 15, 16]
     */
    public MediaFile saveFile(MultipartFile file) throws IOException {
        // Coverage fix: Empty file validation 
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty, cannot save.");
        }

        Path root = Paths.get(uploadDir);
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), root.resolve(fileName));

        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileName(file.getOriginalFilename());
        mediaFile.setFilePath(uploadDir + fileName);
        mediaFile.setFileType(file.getContentType());
        mediaFile.setUploadedAt(LocalDateTime.now());
        
        return repository.save(mediaFile);
    }

    /**
     * PDF se text extract karta hai [cite: 7]
     */
    public String extractTextFromPdf(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IOException("Path is invalid");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("PDF file not found at path: " + filePath);
        }
        
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return (text != null) ? text : ""; 
        }
    }

    /**
     * Metadata update logic [cite: 16]
     */
    public void updateFileMetadata(MediaFile mediaFile) {
        if (mediaFile != null) {
            repository.save(mediaFile);
        }
    }

    /**
     * Saari files fetch karne ke liye (Test compilation fix)
     */
    public List<MediaFile> getAllFiles() {
        return repository.findAll();
    }

    /**
     * Multimedia validation for coverage [cite: 8, 14]
     */
    public boolean isSupportedFile(String contentType) {
        if (contentType == null) return false;
        return contentType.contains("pdf") || 
               contentType.contains("audio") || 
               contentType.contains("video");
    }
}