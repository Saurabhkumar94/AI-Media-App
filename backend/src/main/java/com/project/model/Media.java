package com.project.model;

import java.util.Objects;

public class Media {
    private Long id;
    private String name;

    // --- Constructors ---
    
    // 1. Default Constructor (Frameworks aur JaCoCo ke liye zaroori)
    public Media() {} 

    // 2. Parameterized Constructor
    public Media(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // --- Getters and Setters ---
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // --- Methods for 100% Coverage ---

    // 3. toString Method (Line aur Instruction coverage fix karta hai)
    @Override
    public String toString() {
        return "Media{id=" + id + ", name='" + name + "'}";
    }

    // 4. equals Method (Saari 4 logical branches cover karta hai)
    @Override
    public boolean equals(Object o) {
        // Branch 1: Same memory address (this == o)
        if (this == o) return true;
        
        // Branch 2 & 3: Null check (o == null) aur Class type check (getClass())
        if (o == null || getClass() != o.getClass()) return false;
        
        // Branch 4: Field-level comparison
        Media media = (Media) o;
        return Objects.equals(id, media.id) && 
               Objects.equals(name, media.name);
    }

    // 5. hashCode Method (Data structures ke liye zaroori)
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}