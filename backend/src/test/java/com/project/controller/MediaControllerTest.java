package com.project.controller;

import com.project.service.MediaService;
import com.project.model.Media; 
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(MediaController.class)
public class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaService mediaService;

    // --- Branch 1: Invalid ID Coverage (ID <= 0) ---
    @Test
    public void testGetMediaById_InvalidId() throws Exception {
        // Test with 0
        mockMvc.perform(get("/api/media/0")) 
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid ID"));

        // Test with negative value to cover (id <= 0) fully
        mockMvc.perform(get("/api/media/-1")) 
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid ID"));
    }

    // --- Branch 2: Data Not Found Coverage (data == null) ---
    @Test
    public void testGetMediaById_NotFound() throws Exception {
        Mockito.when(mediaService.findById(100L)).thenReturn(null);

        mockMvc.perform(get("/api/media/100"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not Found"));
    }

    // --- Branch 3: Catch Block Coverage (Exception) ---
    @Test
    public void testGetMediaById_ServerError() throws Exception {
        Mockito.when(mediaService.findById(1L)).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(get("/api/media/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Server Error"));
    }

    // --- Success Path Coverage ---
    @Test
    public void testGetMediaById_Success() throws Exception {
        Media mockMedia = new Media(); 
        mockMedia.setId(1L);
        mockMedia.setName("Test Media");

        Mockito.when(mediaService.findById(1L)).thenReturn(mockMedia);

        mockMvc.perform(get("/api/media/1"))
                .andExpect(status().isOk());
    }

    // --- Added for Extra Coverage (Model & Service Fix) ---
    @Test
    public void testExtraCoverage() {
        Media m1 = new Media();
        m1.setId(5L);
        m1.setName("Full Coverage");
        
        Media m2 = new Media(10L, "Constructor Check");
        
        assertEquals(5L, m1.getId());
        assertEquals("Full Coverage", m1.getName());
        assertEquals(10L, m2.getId());
        
        MediaService realService = new MediaService();
        assertNull(realService.findById(null));
        assertNull(realService.findById(-1L));
        assertNotNull(realService.findById(1L));
    }

    @Test
    public void testMediaModelFullCoverage() {
        Media m1 = new Media(1L, "Test");
        Media m2 = new Media(1L, "Test");
        Media m3 = new Media(2L, "Different");
        
        assertTrue(m1.equals(m2));
        assertFalse(m1.equals(m3));
        assertFalse(m1.equals(null));
        assertEquals(m1.hashCode(), m2.hashCode());
        assertNotNull(m1.toString());
    }

    @Test
    public void testMediaModelFullBranchCoverage() {
        Media m1 = new Media(1L, "Test");
        Media m2 = new Media(1L, "Test");

        assertTrue(m1.equals(m1));
        assertTrue(m1.equals(m2));
        assertFalse(m1.equals(null));
        assertFalse(m1.equals("Not Media")); 
        
        assertEquals(m1.hashCode(), m2.hashCode());
        assertNotNull(m1.toString());
    }

    @Test
    public void testFinalCoverageBoost() {
        Media m1 = new Media(1L, "Test");
        Media m2 = new Media(1L, "Test");
        
        assertTrue(m1.equals(m1));
        assertFalse(m1.equals(null));
        assertFalse(m1.equals("String"));
        assertEquals(m1.hashCode(), m2.hashCode());
        assertNotNull(m1.toString());

        MediaService realService = new MediaService();
        assertNull(realService.findById(null)); 
        assertNull(realService.findById(0L));   
        assertNotNull(realService.findById(1L)); 
    }

    // --- STEP 1: Fixes Service Layer Branches (if conditions) ---
    @Test
    public void testServiceFinalFix() {
        MediaService service = new MediaService();
        
        assertNull(service.findById(null)); 
        assertNull(service.findById(0L));   
        assertNull(service.findById(-1L));  
        assertNotNull(service.findById(1L)); 
    }

    // --- STEP 2: Fixes Model Branch (equals method logic) ---
    @Test
    public void testModelFinalFix() {
        Media m1 = new Media(1L, "Test");
        Media m2 = new Media(1L, "Test");
        
        assertTrue(m1.equals(m1));           
        assertTrue(m1.equals(m2));           
        assertFalse(m1.equals(null));        
        assertFalse(m1.equals("Not Media")); 
        
        Media m3 = new Media(2L, "Different");
        assertFalse(m1.equals(m3));          

        assertEquals(m1.hashCode(), m2.hashCode());
        assertNotNull(m1.toString());
    }

    // --- STEP 3: Fixes Controller Missed Branches ---
    @Test
    public void testControllerBranchFix() throws Exception {
        mockMvc.perform(get("/api/media/-5"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid ID"));

        Mockito.when(mediaService.findById(999L)).thenReturn(null);
        mockMvc.perform(get("/api/media/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not Found"));

        Mockito.when(mediaService.findById(Mockito.anyLong())).thenThrow(new RuntimeException("System Error"));
        mockMvc.perform(get("/api/media/50"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Server Error"));
    }

    @Test
    public void testServicePackageFullCoverage() {
        MediaService realService = new MediaService();
        assertNull(realService.findById(null));
        assertNull(realService.findById(0L));
        assertNull(realService.findById(-1L));

        Media result = realService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Sample Media", result.getName());
    }

    @Test
    public void testModelAndServiceDirectly() {
        MediaService realService = new MediaService();
        assertNull(realService.findById(null)); 
        assertNull(realService.findById(0L));   
        assertNull(realService.findById(-1L));  
        
        Media result = realService.findById(1L); 
        assertNotNull(result);
        assertEquals(1L, result.getId());

        Media m1 = new Media(1L, "Test");
        Media m2 = new Media(1L, "Test");
        Media m3 = new Media(2L, "Different");

        assertTrue(m1.equals(m1));           
        assertFalse(m1.equals(null));        
        assertFalse(m1.equals("String"));    
        assertTrue(m1.equals(m2));           
        assertFalse(m1.equals(m3));          
        
        assertNotNull(m1.hashCode());
        assertNotNull(m1.toString());
    }

    // --- FINAL MASTER FIX FOR 95%+ COVERAGE ---
    @Test
    public void finalizeCoverage() {
        // 1. Service Fix (85% -> 100%) - Using direct instance to hit branches 
        MediaService service = new MediaService();
        assertNull(service.findById(null));
        assertNull(service.findById(0L));
        assertNull(service.findById(-1L));
        assertNotNull(service.findById(1L));

        // 2. Model Fix (90% -> 100%) - Hitting all logical branches in equals() 
        Media m1 = new Media(1L, "A");
        Media m2 = new Media(1L, "A");
        Media m3 = new Media(2L, "B");
        
        assertTrue(m1.equals(m1));           // Same object
        assertFalse(m1.equals(null));        // Null check
        assertFalse(m1.equals("String"));    // Different class
        assertTrue(m1.equals(m2));           // Field match
        assertFalse(m1.equals(m3));          // Different ID
        assertEquals(m1.hashCode(), m2.hashCode());
        assertNotNull(m1.toString());
    }
}