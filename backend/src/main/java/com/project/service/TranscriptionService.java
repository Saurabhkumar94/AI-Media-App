package com.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;

@Service
public class TranscriptionService {

    @Value("${deepgram.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.deepgram.com/v1/listen")
            .build();

    public String transcribeMedia(String filePath) {
        try {
            // Deepgram API call for transcription with timestamps 
            return webClient.post()
                    .header("Authorization", "Token " + apiKey)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .bodyValue(new FileSystemResource(filePath))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            return "Transcription Error: " + e.getMessage();
        }
    }
}