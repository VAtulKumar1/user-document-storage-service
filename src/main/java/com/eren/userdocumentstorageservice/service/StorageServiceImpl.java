package com.eren.userdocumentstorageservice.service;

import com.eren.userdocumentstorageservice.dto.S3ObjectDTO;
import com.eren.userdocumentstorageservice.request.DownloadRequest;
import com.eren.userdocumentstorageservice.request.SearchRequest;
import com.eren.userdocumentstorageservice.response.DownloadResponse;
import com.eren.userdocumentstorageservice.response.SearchResponse;
import com.eren.userdocumentstorageservice.util.ConvertMultipartFileToObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {
    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private S3Client amazonS3Client;

    public String uploadFile(String userName,MultipartFile file) throws IOException {
        ConvertMultipartFileToObject convert= new ConvertMultipartFileToObject();
        File fileObj = convert.convertMultipartFileToFileObjet(file);
        String fileName = userName+"/"+file.getOriginalFilename();
        PutObjectRequest request=PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        amazonS3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        fileObj.delete();
        return "Uploaded File "+ file.getOriginalFilename();

    }

    public SearchResponse searchFile(SearchRequest request){
        String userName = request.getUserName();
        String fileName = request.getFileName();
        int limit = request.getLimit();
        String continuationToken = request.getContinuationToken();

        String prefix = userName+"/"+fileName;
        ListObjectsV2Request.Builder requestBuilder= ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .maxKeys(limit);
        if(continuationToken!=null){
            requestBuilder.continuationToken(continuationToken);
        }
        ListObjectsV2Response s3Response  = amazonS3Client.listObjectsV2(requestBuilder.build());
        List<S3ObjectDTO> contents = s3Response.contents().stream()
                .map(s3Object -> new S3ObjectDTO(s3Object.key(), s3Object.size()))
                .collect(Collectors.toList());;
        String nextContinuationToken = s3Response.nextContinuationToken();
        SearchResponse response = new SearchResponse();
        response.setContents(contents);
        response.setContinutaionToken(nextContinuationToken);
        return response;
    }

    @Override
    public DownloadResponse downloadFile(DownloadRequest request) throws IOException {
        String userName = request.getUserName();
        String fileName = request.getFileName();
        String path =userName + "/" + fileName;



        GetObjectRequest requestBuilder = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(path)
                .build();

        ResponseInputStream<GetObjectResponse> responseInputStream = amazonS3Client.getObject(requestBuilder);
        GetObjectResponse getObjectResponse = responseInputStream.response();
        DownloadResponse downloadResponse = DownloadResponse.builder()
                .fileContent(IoUtils.toByteArray(responseInputStream))
                .contentType(getObjectResponse.contentType())
                .originalFilename(fileName)
                .build();

        return downloadResponse;

    }





}
