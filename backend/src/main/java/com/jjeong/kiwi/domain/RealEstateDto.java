package com.jjeong.kiwi.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class RealEstateDto {
    private String title;
    private String description;
    private int price;
    private MultipartFile image;
    private boolean soldout;
}
