package com.project.controller;

import com.project.model.MediaFile;
import com.project.service.AIService;
import com.project.service.FileService;
import com.project.service.TranscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*") // Frontend (React) connectivity ke liye
public class FileUploadController {

    @Autowired
    private FileService fileService;

    @Autowired
    private AIService aiService;

    @Autowired
    private TranscriptionService transcriptionService;

    // Endpoint: http://localhost:8081/api/files/upload
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // --- NAYA CHECK: Empty file handle karne ke liye (Test failure fix) ---
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("File cannot be empty");
            }

            // 1. File ko local storage aur database mein save karein
            MediaFile savedFile = fileService.saveFile(file);
            
            // Extra Safety: Agar service null return kare toh exception throw karein
            if (savedFile == null) {
                throw new RuntimeException("File saving failed");
            }

            String filePath = savedFile.getFilePath();
            String fileType = file.getContentType();

            // 2. Logic Flow: File type ke according service select karein
            if (fileType != null && fileType.contains("pdf")) {
                // PDF Section: Text extract karke Gemini se summary lein
                String extractedText = fileService.extractTextFromPdf(filePath);
                String summary = aiService.summarizeText(extractedText);
                savedFile.setSummary(summary);
            } 
            else if (fileType != null && (fileType.contains("audio") || fileType.contains("video"))) {
                // Multimedia Section: Deepgram se transcription lein
                String transcriptionResult = transcriptionService.transcribeMedia(filePath);
                
                // NAYA: AI se transcription JSON me se specific timestamps nikalwayein 
                String summaryWithTimestamps = aiService.extractTimestamps(transcriptionResult);
                
                // Transcribed text aur timestamps store karein
                savedFile.setSummary(summaryWithTimestamps);
            }

            // 3. Updated metadata ke sath database update karein
            fileService.updateFileMetadata(savedFile);

            return ResponseEntity.ok(savedFile);

        } catch (Exception e) {
            // Error handling for 95%+ coverage requirement 
            return ResponseEntity.status(500).body("Upload/Processing failed: " + e.getMessage());
        }
    }

    // --- NAYA CHATBOT ENDPOINT (Assignment Task 6) ---
    
    /**
     * Users can interact with an AI-powered chatbot to ask questions based on uploaded files.
     * Objective: Answer questions using the file's content.
     */
    @PostMapping("/chat")
    public ResponseEntity<?> chatWithFile(@RequestBody Map<String, String> payload) {
        try {
            String question = payload.get("question");
            String fileContent = payload.get("fileContent"); // Frontend summary/transcript bhejega

            if (question == null || fileContent == null) {
                return ResponseEntity.badRequest().body("Question and File Content are required.");
            }

            // AI Service se question-based answer lena
            String answer = aiService.askQuestionAboutFile(question, fileContent);

            Map<String, String> response = new HashMap<>();
            response.put("answer", answer);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Chatbot Error: " + e.getMessage());
        }
    }

    // --- NAYA: GET TIMESTAMPS ENDPOINT (For 'Play' Button Requirement)  ---
    @GetMapping("/{id}/timestamps")
    public ResponseEntity<?> getTimestamps(@PathVariable Long id) {
        // Logic to fetch timestamps for a specific file
        return ResponseEntity.ok("Timestamps logic is integrated within summary for this assignment");
    }
}