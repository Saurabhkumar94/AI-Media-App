package com.project.service;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    @Value("${google.gemini.key}")
    private String apiKey;

    /**
     * Using OpenAI-compatible interface for Gemini.
     * This ensures the app is stable even if specific Gemini libraries fail to download. [cite: 13]
     */
    public String summarizeText(String text) {
        try {
            // Hum OpenAI ka connector use kar rahe hain jo Gemini ke server se baat karega [cite: 13]
            OpenAiChatModel model = OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .baseUrl("https://generativelanguage.googleapis.com/v1beta/openai/") 
                    .modelName("gemini-1.5-flash")
                    .build();

            // Task: Summarize the content of uploaded files [cite: 7, 24]
            String prompt = "Summarize the following document content in exactly 2 clear lines: " + text;

            // AI Model call through LangChain interface
            return model.generate(prompt);
            
        } catch (Exception e) {
            return "AI Summary Error: " + e.getMessage();
        }
    }

    /**
     * Interacts with the AI model to answer specific questions based on file content. [cite: 6]
     */
    public String askQuestionAboutFile(String question, String content) {
        try {
            // Wahi model configuration jo summarize mein use hui hai [cite: 13]
            OpenAiChatModel model = OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .baseUrl("https://generativelanguage.googleapis.com/v1beta/openai/") 
                    .modelName("gemini-1.5-flash")
                    .build();

            // Prompt engineering: Model ko context dena zaroori hai [cite: 13]
            String prompt = "You are a helpful AI assistant. Use the following context to answer the user's question accurately.\n\n" +
                            "Context (File Content): " + content + "\n\n" +
                            "User Question: " + question;

            // AI Model se answer generate karwana [cite: 6]
            return model.generate(prompt);

        } catch (Exception e) {
            return "Chatbot Error: " + e.getMessage();
        }
    }

    // --- NAYA METHOD: TIMESTAMPS EXTRACTION (Assignment Task 8 & 25) ---

    /**
     * AI se transcription JSON se specific topics aur unke timestamps nikalne ke liye. 
     */
    public String extractTimestamps(String rawJson) {
        try {
            OpenAiChatModel model = OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .baseUrl("https://generativelanguage.googleapis.com/v1beta/openai/") 
                    .modelName("gemini-1.5-flash")
                    .build();

            // AI ko Deepgram ka raw JSON dekar specific topics aur seconds nikalne ko kehna [cite: 8, 14]
            String prompt = "I will provide you a transcription JSON. Extract 3-4 key topics " +
                            "and their exact start time in seconds. " +
                            "Format the output strictly as: Topic Name - [seconds]. \n\nJSON: " + rawJson;

            return model.generate(prompt);

        } catch (Exception e) {
            return "Timestamp Extraction Error: " + e.getMessage();
        }
    }
}