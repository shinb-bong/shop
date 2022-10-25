package com.shop.entity.item.image;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
/**
 * 원본 이미지 파일은 DB에 저장되는 것이 아니기때문에 Repository 불필요
 *
 */
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName,
                             byte[] fileData) throws Exception{

        //UUID를 이용하여 파일명 새로 생성
        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension; // 최종 파일명

        // 경로 + 파일명
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;

        // FileOutputStream 객체를 이용하여 경로 지정후 저장
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();

        return savedFileName;

    }

    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath);

        if (deleteFile.exists()){
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else{
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
