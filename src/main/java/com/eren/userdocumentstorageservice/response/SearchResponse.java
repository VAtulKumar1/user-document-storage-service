package com.eren.userdocumentstorageservice.response;


import com.eren.userdocumentstorageservice.dto.S3ObjectDTO;
import lombok.Data;

import java.util.List;



@Data
public class SearchResponse {
    private List<S3ObjectDTO> contents;
    private String continutaionToken;
}
