package com.tclk.fileupload.repository;

import com.tclk.fileupload.entity.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageDataRepository extends JpaRepository<ImageData,Long> {
}
