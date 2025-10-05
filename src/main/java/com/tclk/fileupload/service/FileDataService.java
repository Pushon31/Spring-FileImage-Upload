package com.tclk.fileupload.service;

import com.tclk.fileupload.dto.FileInfo;
import com.tclk.fileupload.entity.FileData;
import com.tclk.fileupload.repository.FileDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileDataService {

    @Autowired
    private FileDataRepository fileDataRepository;

    // Better to use property injection rather than hardcoding
    private final String FOLDER_PATH = "D:\\pushon 1288565\\Picture";

    @Transactional
    public String uploadImageToFileSystem(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        // Sanitize original filename
        String rawFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (rawFilename.contains("..")) {
            throw new IOException("Filename has invalid path sequence: " + rawFilename);
        }

        // Extract extension, if any
        String extension = "";
        int dotIndex = rawFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = rawFilename.substring(dotIndex);
        }

        // Generate unique filename
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // Make sure the upload directory exists
        Path uploadDir = Paths.get(FOLDER_PATH).toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);

        // Target path for saving file
        Path targetPath = uploadDir.resolve(uniqueFilename);

        // Save metadata into DB
        FileData fileData = FileData.builder()
                .fileName(uniqueFilename)
                .type(file.getContentType())
                .filePath(targetPath.toString())
                .build();

        FileData saved = fileDataRepository.save(fileData);

        // Save the physical file
        file.transferTo(targetPath.toFile());
        // Alternatively:
        // Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "File uploaded successfully: " + saved.getFilePath();
    }

    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        FileData fileData = fileDataRepository.findAllSortedByNameUsingNative(fileName);
        if (fileData == null) {
            throw new FileNotFoundException("File not found: " + fileName);
        }
        return Files.readAllBytes(new File(fileData.getFilePath()).toPath());
    }

    public List<FileInfo> getAllImage(){
        List<FileInfo> fileInfos = new ArrayList<>();
        List<FileData> fileDataList = fileDataRepository.findAll();
        fileDataList.forEach(fileData -> {
            try {
                byte[] image = Files.readAllBytes(new File(fileData.getFilePath()).toPath());
                FileInfo fn = new FileInfo(fileData.getFileName(),fileData.getFilePath(),image);
                fileInfos.add(fn);
            }
            catch (IOException e) {
                throw new RuntimeException("Error reading file" + fileData.getFileName(),e);
            }
        });
        return fileInfos;
    }

    public void deleteByName(String fileName){
        FileData fileData = fileDataRepository.findByName(fileName)
                .orElseThrow(()-> new RuntimeException("File not found" + fileName));

        File file = new File(fileData.getFilePath());
        if(file.exists() && !file.delete()){
            throw new RuntimeException("Unable to delete file: " + fileName);
        }
        fileDataRepository.delete(fileData);
    }


}
