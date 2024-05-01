package com.eren.userdocumentstorageservice.controller;

import com.eren.userdocumentstorageservice.request.DownloadRequest;
import com.eren.userdocumentstorageservice.request.SearchRequest;
import com.eren.userdocumentstorageservice.response.DownloadResponse;
import com.eren.userdocumentstorageservice.response.SearchResponse;
import com.eren.userdocumentstorageservice.service.StorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/v1/storage-service")
@Slf4j
public class StorageController{

    @Autowired
    private StorageServiceImpl service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam String userName,@RequestParam("file") MultipartFile file) throws IOException {
        if(file==null || file.isEmpty()){
            log.error("file not present");
            return  new ResponseEntity<>("file not present",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(service.uploadFile(userName,file),HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<SearchResponse> searchFile(@RequestBody SearchRequest request) throws IOException {
        SearchResponse searchResponse= service.searchFile(request);
        return new ResponseEntity<>(searchResponse,HttpStatus.OK);
    }

    @PostMapping("/download")
    public ResponseEntity<DownloadResponse> downloadFile(@RequestBody DownloadRequest request) throws IOException, URISyntaxException {
        return new ResponseEntity<>(service.downloadFile(request),HttpStatus.OK);
    }
}
