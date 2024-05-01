package com.eren.userdocumentstorageservice.service;

import com.eren.userdocumentstorageservice.request.DownloadRequest;
import com.eren.userdocumentstorageservice.request.SearchRequest;
import com.eren.userdocumentstorageservice.response.DownloadResponse;
import com.eren.userdocumentstorageservice.response.SearchResponse;
import com.eren.userdocumentstorageservice.util.ConvertMultipartFileToObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class StorageServiceTest {
    @Mock
    private S3Client amazonS3Client;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private StorageServiceImpl storageServiceImpl;



    @Test
    public void testUploadFile() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("test-file.txt");
        when(multipartFile.getBytes()).thenReturn(new byte[10]);
        String result = storageServiceImpl.uploadFile("K K Menon", multipartFile);
        assertEquals("Uploaded File test-file.txt", result);
        verify(amazonS3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }


    @Test
    public void testUploadFile_FileConversionError() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("test-file.txt");
        when(multipartFile.getBytes()).thenThrow(new IOException("Failed to convert"));

        assertThrows(IOException.class, () -> {
            storageServiceImpl.uploadFile("Diljeet", multipartFile);
        });
    }

    @Test
    public void testUploadFile_VerifyTemporaryFileDeletion() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("test-file.txt");
        when(multipartFile.getBytes()).thenReturn(new byte[10]);

        ConvertMultipartFileToObject converter = new ConvertMultipartFileToObject();
        File file = converter.convertMultipartFileToFileObjet(multipartFile);

        storageServiceImpl.uploadFile("A K Verma", multipartFile);

        assertFalse(file.exists(), "Temporary file should be deleted after upload");
    }


    @Test
    public void testSearchFile_WithResults() {
        SearchRequest request = new SearchRequest();
        request.setUserName("Barsha");
        request.setFileName("report.pdf");
        request.setLimit(10);

        List<S3Object> s3Objects = new ArrayList<>();
        s3Objects.add(createS3Object("Barsha/report.pdf", 1024));

        ListObjectsV2Response s3Response = ListObjectsV2Response.builder()
                .contents(s3Objects)
                .build();

        when(amazonS3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(s3Response);

        SearchResponse response = storageServiceImpl.searchFile(request);

        assertNotNull(response);
        assertNotNull(response.getContents());
        assertEquals(1, response.getContents().size());
        assertEquals("Barsha/report.pdf", response.getContents().get(0).getKey());
    }

    @Test
    public void testSearchFile_NoResults() {
        SearchRequest request = new SearchRequest();
        request.setUserName("Sahrukh");
        request.setFileName("nonexistent.pdf");
        request.setLimit(10);

        ListObjectsV2Response s3Response = ListObjectsV2Response.builder()
                .contents(new ArrayList<>())
                .build();

        when(amazonS3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(s3Response);

        SearchResponse response = storageServiceImpl.searchFile(request);

        assertNotNull(response);
        assertTrue(response.getContents().isEmpty());
    }

    @Test
    public void testSearchFile_WithContinuationToken() {
        SearchRequest request = new SearchRequest();
        request.setUserName("Rokcy");
        request.setFileName("KGF3.pdf");
        request.setLimit(10);
        request.setContinuationToken("KGF2");

        List<S3Object> s3Objects = new ArrayList<>();
        s3Objects.add(createS3Object("Rokcy/KGF3.pdf", 1024));

        ListObjectsV2Response s3Response = ListObjectsV2Response.builder()
                .contents(s3Objects)
                .nextContinuationToken("nextToken")
                .build();

        when(amazonS3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(s3Response);

        SearchResponse response = storageServiceImpl.searchFile(request);

        assertNotNull(response);
        assertEquals("nextToken", response.getContinutaionToken());
    }

    private S3Object createS3Object(String key, long size) {
        return S3Object.builder().key(key).size(size).build();
    }


    @Test
    public void testDownloadFile_Success() throws IOException {
        DownloadRequest downloadRequest = new DownloadRequest();
        downloadRequest.setUserName("K K Menon");
        downloadRequest.setFileName("report.pdf");

        ByteArrayInputStream mockInputStream = new ByteArrayInputStream("Mock file content".getBytes());

        GetObjectResponse getObjectResponse = GetObjectResponse.builder()
                .contentType("application/pdf")
                .build();

        ResponseInputStream<GetObjectResponse> responseInputStream = new ResponseInputStream<>(getObjectResponse, mockInputStream);

        when(amazonS3Client.getObject(any(GetObjectRequest.class)))
                .thenReturn(responseInputStream);

        DownloadResponse downloadResponse = storageServiceImpl.downloadFile(downloadRequest);

        assertNotNull(downloadResponse);
        assertEquals("report.pdf", downloadResponse.getOriginalFilename());
        assertEquals("application/pdf", downloadResponse.getContentType());
        assertEquals("Mock file content", new String(downloadResponse.getFileContent()));
    }



    @Test
    public void testDownloadFile_IOError() {
        DownloadRequest downloadRequest = new DownloadRequest();
        downloadRequest.setUserName("john");
        downloadRequest.setFileName("report.pdf");

        when(amazonS3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(new IOException("IO error during download"));

        assertThrows(IOException.class, () -> {
            storageServiceImpl.downloadFile(downloadRequest);
        });
    }

}
