package com.eren.userdocumentstorageservice.request;

import lombok.Data;

@Data
public class SearchRequest {
    private String userName;
    private String fileName;
    private int limit;
    private String continuationToken;
}
