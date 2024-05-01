package com.eren.userdocumentstorageservice.service;

import com.eren.userdocumentstorageservice.request.DownloadRequest;
import com.eren.userdocumentstorageservice.request.SearchRequest;
import com.eren.userdocumentstorageservice.response.DownloadResponse;
import com.eren.userdocumentstorageservice.response.SearchResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;

public interface StorageService {

    public String uploadFile(String userName, MultipartFile file) throws IOException;

    public SearchResponse searchFile(SearchRequest request);

    public DownloadResponse downloadFile(DownloadRequest request) throws URISyntaxException, IOException;
}
