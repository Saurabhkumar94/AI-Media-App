package com.project.repository; // 'Java.' hata diya hai standard structure ke liye

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.model.MediaFile;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
    // Ab ye FileService ki errors ko khatam kar dega
}