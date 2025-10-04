package com.tclk.fileupload.controller;

import com.tclk.fileupload.message.ResponseMessage;
import com.tclk.fileupload.service.FileDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileDataController {

    @Autowired
    private FileDataService fileDataService;

    @PostMapping("/fileSystem")
    public ResponseEntity<ResponseMessage> uploadImageToFileSystem(
            @RequestParam("image") MultipartFile file) {
        String message;
        try {
            String result = fileDataService.uploadImageToFileSystem(file);
            message = "Upload Success : " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload file: " + file.getOriginalFilename()
                    + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseMessage(message));
        }
    }

    @GetMapping("/fileSystem/{fileName}")
    public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
        byte[] imageData = fileDataService.downloadImageFromFileSystem(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }
}
