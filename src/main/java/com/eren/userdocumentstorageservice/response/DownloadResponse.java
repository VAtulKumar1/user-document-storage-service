package com.eren.userdocumentstorageservice.response;


import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@RequiredArgsConstructor
public class DownloadResponse {
    private final byte[] fileContent;
    private final String originalFilename;
    private final String contentType;
}
