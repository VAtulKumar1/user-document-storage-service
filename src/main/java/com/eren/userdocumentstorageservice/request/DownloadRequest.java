package com.eren.userdocumentstorageservice.request;

import lombok.Data;

@Data
public class DownloadRequest {
    private String userName;
    private String fileName;
}
