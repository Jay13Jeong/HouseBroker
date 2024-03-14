package com.jjeong.kiwi.tool;

import com.google.common.hash.Hashing;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;
import org.springframework.stereotype.Component;

public class HashProvider {
    public String getMurmurHash(String data) {
        try {
            byte[] fileData = data.getBytes();
            // MurmurHash 128-bit
            return Hashing.murmur3_128()
                .hashBytes(fileData)
                .toString(); // 해시 값을 String으로 변환합니다.
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    public String getSHA256(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] fileData = data.getBytes();
            md.update(fileData);

            byte[] digest = md.digest();

            // 바이트 배열을 HEX 문자열로 변환.
            return DatatypeConverter.printHexBinary(digest).toLowerCase();
        }catch (Exception e){
            throw new RuntimeException();
        }
    }
}
