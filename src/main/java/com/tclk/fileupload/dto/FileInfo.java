package com.tclk.fileupload.dto;

import lombok.Data;

@Data
public class FileInfo {
    private String name;
    private String url;
    private  byte[] image;

    public FileInfo(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public FileInfo() {
    }

    public FileInfo(String name, byte[] image) {
        this.name = name;
        this.image = image;
    }

    public FileInfo(String name, String url, byte[] image) {
        this.name = name;
        this.url = url;
        this.image = image;
    }
}
