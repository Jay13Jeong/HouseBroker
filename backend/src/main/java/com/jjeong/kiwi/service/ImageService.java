package com.jjeong.kiwi.service;

import com.google.common.hash.Hashing;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    public String calculateMurmurHash(MultipartFile file) {
        try {
            byte[] fileData = file.getBytes();
            // MurmurHash 128-bit
            return Hashing.murmur3_128()
                .hashBytes(fileData)
                .toString(); // 해시 값을 String으로 변환합니다.
        }catch (Exception e){
            logger.error("calculateMurmurHash", e);
            throw new RuntimeException("500:calculateMurmurHash:fail to gen hash");
        }
    }

    public String calculateSHA256(MultipartFile file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] fileData = file.getBytes();
            md.update(fileData);

            byte[] digest = md.digest();

            // 바이트 배열을 HEX 문자열로 변환.
            return DatatypeConverter.printHexBinary(digest).toLowerCase();
        }catch (Exception e){
            logger.error("calculateSHA256", e);
            throw new RuntimeException("500:calculateSHA256:fail to gen hash");
        }
    }
}
