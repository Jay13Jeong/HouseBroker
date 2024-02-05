package com.jjeong.kiwi.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class RealEstateDto{
    private MultipartFile image;
    private MultipartFile image2;
    private MultipartFile image3;
    private MultipartFile image4;
    private MultipartFile image5;
    private MultipartFile image6;
    private MultipartFile image7;
    private MultipartFile image8;
    private MultipartFile image9;
    private MultipartFile image10;
    private List<Integer> imageSlotState;
    private String title;
    private String description;
    private Long price;
    private boolean soldout; //판매여부
    private String relay_object_type; //중계대상물종류
    private String location; //소재지
    private Long area; //면적(제곱미터)
    private String transaction_type; //거래형태
    private String residence_availability_date; //입주가능일
    private String administrative_agency_approval_date; //행정기관승인날짜
    private Long number_of_cars_parked; //주차대수
    private String direction; //방향
    private Long administration_cost; //관리비
    private Long administration_cost2; //관리비
    private Double latitude; //위도
    private Double longitude; //경도
}
