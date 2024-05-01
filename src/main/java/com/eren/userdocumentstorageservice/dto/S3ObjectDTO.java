package com.eren.userdocumentstorageservice.dto;

public class S3ObjectDTO {
    private String key;
    private long size;

    public S3ObjectDTO(String key, long size) {
        this.key = key;
        this.size = size;
    }

    // Getters and Setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
