package com.eren.userdocumentstorageservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
@Slf4j
public class ConvertMultipartFileToObject {
    public File convertMultipartFileToFileObjet(MultipartFile file)  {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try(FileOutputStream fileOutputStream= new FileOutputStream(convertedFile)){
            fileOutputStream.write(file.getBytes());
        }catch(IOException e){
            log.error("Some Error Occured",e);
        }

        return convertedFile;

    }
}
