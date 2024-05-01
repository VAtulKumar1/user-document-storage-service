package com.eren.userdocumentstorageservice.controller;

import com.eren.userdocumentstorageservice.request.DownloadRequest;
import com.eren.userdocumentstorageservice.request.SearchRequest;
import com.eren.userdocumentstorageservice.response.DownloadResponse;
import com.eren.userdocumentstorageservice.response.SearchResponse;
import com.eren.userdocumentstorageservice.service.StorageServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {StorageController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class StorageControllerTest {

    @Autowired
    private StorageController storageController;

    @MockBean
    private StorageServiceImpl storageServiceImpl;

    @Test
    void testUploadFile() throws IOException {

        ResponseEntity<String> actualUploadFileResult = (new StorageController()).uploadFile("Saitama", null);
        assertEquals("file not present", actualUploadFileResult.getBody());
        assertEquals(400, actualUploadFileResult.getStatusCodeValue());
        assertTrue(actualUploadFileResult.getHeaders().isEmpty());
    }

    @Test
    void testUploadFile2() throws IOException {
        StorageController storageController = new StorageController();

        ResponseEntity<String> actualUploadFileResult = storageController.uploadFile("Eren The Dev",
                new MockMultipartFile("Name", new ByteArrayInputStream(new byte[]{})));

        assertEquals("file not present", actualUploadFileResult.getBody());
        assertEquals(400, actualUploadFileResult.getStatusCodeValue());
        assertTrue(actualUploadFileResult.getHeaders().isEmpty());
    }

    @Test
    void testSearchFile() throws Exception {

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setContents(new ArrayList<>());
        searchResponse.setContinutaionToken("ABC123");
        when(storageServiceImpl.searchFile(Mockito.<SearchRequest>any())).thenReturn(searchResponse);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setContinuationToken("ABC123");
        searchRequest.setFileName("OGSongs.txt");
        searchRequest.setLimit(1);
        searchRequest.setUserName("KK Menon");
        String content = (new ObjectMapper()).writeValueAsString(searchRequest);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/storage-service/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        MockMvcBuilders.standaloneSetup(storageController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"contents\":[],\"continutaionToken\":\"ABC123\"}"));
    }


    @Test
    void testDownloadFile() throws Exception {

        DownloadResponse.DownloadResponseBuilder contentTypeResult = DownloadResponse.builder().contentType("text/plain");
        DownloadResponse buildResult = contentTypeResult.fileContent("AXAXAXAX".getBytes("UTF-8"))
                .originalFilename("food car.txt")
                .build();
        when(storageServiceImpl.downloadFile(Mockito.<DownloadRequest>any())).thenReturn(buildResult);

        DownloadRequest downloadRequest = new DownloadRequest();
        downloadRequest.setFileName("food car.txt");
        downloadRequest.setUserName("Rameswram Bhojanalaya");
        String content = (new ObjectMapper()).writeValueAsString(downloadRequest);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/storage-service/download")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);


        MockMvcBuilders.standaloneSetup(storageController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"fileContent\":\"QVhBWEFYQVg=\",\"originalFilename\":\"food car.txt\",\"contentType\":\"text/plain\"}"));
    }
}
