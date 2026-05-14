package com.project.controller;

import com.project.model.MediaFile;
import com.project.service.AIService;
import com.project.service.FileService;
import com.project.service.TranscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private AIService aiService;

    @MockBean
    private TranscriptionService transcriptionService;

    @Test
    void testUploadSuccess_PDF() throws Exception {
        MediaFile mockFile = new MediaFile();
        mockFile.setFilePath("test.pdf");
        
        when(fileService.saveFile(any())).thenReturn(mockFile);
        when(fileService.extractTextFromPdf(anyString())).thenReturn("text");
        when(aiService.summarizeText(anyString())).thenReturn("summary");

        MockMultipartFile pdfFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "data".getBytes());
        
        mockMvc.perform(multipart("/api/files/upload").file(pdfFile))
               .andExpect(status().isOk());
    }

    @Test
    void testUpload_InvalidFileType() throws Exception {
        MediaFile mockFile = new MediaFile();
        mockFile.setFilePath("test.txt");
        
        when(fileService.saveFile(any())).thenReturn(mockFile);

        MockMultipartFile txtFile = new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes());
        
        mockMvc.perform(multipart("/api/files/upload").file(txtFile))
               .andExpect(status().isOk());
    }

    @Test
    void testChat_BadRequest_MissingParams() throws Exception {
        mockMvc.perform(post("/api/files/chat")
               .contentType("application/json")
               .content("{}"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void testUpload_ServerError() throws Exception {
        when(fileService.saveFile(any())).thenThrow(new RuntimeException("Database Down"));

        MockMultipartFile pdfFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "data".getBytes());
        
        mockMvc.perform(multipart("/api/files/upload").file(pdfFile))
               .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetTimestamps_Coverage() throws Exception {
        mockMvc.perform(get("/api/files/1/timestamps"))
               .andExpect(status().isOk());
    }

    @Test
    void testUploadSuccess_VideoAudio() throws Exception {
        MediaFile mockFile = new MediaFile();
        mockFile.setFilePath("test.mp4");
        
        when(fileService.saveFile(any())).thenReturn(mockFile);
        when(transcriptionService.transcribeMedia(anyString())).thenReturn("raw json");
        when(aiService.extractTimestamps(anyString())).thenReturn("timestamps summary");

        MockMultipartFile videoFile = new MockMultipartFile("file", "test.mp4", "video/mp4", "video data".getBytes());
        
        mockMvc.perform(multipart("/api/files/upload").file(videoFile))
               .andExpect(status().isOk());
    }

    @Test
    void testChat_Success() throws Exception {
        when(aiService.askQuestionAboutFile(anyString(), anyString())).thenReturn("AI Answer");

        mockMvc.perform(post("/api/files/chat")
               .contentType("application/json")
               .content("{\"question\":\"hello\", \"fileContent\":\"some context\"}"))
               .andExpect(status().isOk());
    }

    @Test
    void testChat_InternalError() throws Exception {
        when(aiService.askQuestionAboutFile(anyString(), anyString())).thenThrow(new RuntimeException("AI Down"));

        mockMvc.perform(post("/api/files/chat")
               .contentType("application/json")
               .content("{\"question\":\"hello\", \"fileContent\":\"context\"}"))
               .andExpect(status().isInternalServerError());
    }

    // --- FINAL BRANCH HITTERS FOR 100% BRANCH COVERAGE ---

    @Test
    void testChat_MissingQuestionOnly() throws Exception {
        mockMvc.perform(post("/api/files/chat")
               .contentType("application/json")
               .content("{\"fileContent\":\"Some content\"}"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void testChat_MissingContentOnly() throws Exception {
        mockMvc.perform(post("/api/files/chat")
               .contentType("application/json")
               .content("{\"question\":\"What is this?\"}"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void testUpload_FileServiceReturnsNull() throws Exception {
        when(fileService.saveFile(any())).thenReturn(null);
        MockMultipartFile pdfFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "data".getBytes());

        mockMvc.perform(multipart("/api/files/upload").file(pdfFile))
               .andExpect(status().isInternalServerError()); 
    }

    // --- NAYA: EXTRA BRANCH COVERAGE ---

    @Test
    void testUpload_EmptyFile_BadRequest() throws Exception {
        // Empty file bhej kar validation branch cover karna
        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "application/pdf", new byte[0]);
        mockMvc.perform(multipart("/api/files/upload").file(emptyFile))
               .andExpect(status().isBadRequest());
    }
}