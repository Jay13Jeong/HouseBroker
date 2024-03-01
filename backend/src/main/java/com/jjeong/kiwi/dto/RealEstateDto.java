package com.jjeong.kiwi.dto;

import com.jjeong.kiwi.model.RealEstate;
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
    private Long id; //매물 번호
    private String title; // 매물이름
    private String description; //매물 설명
    private Long price; //매물 가격
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

    public RealEstateDto(RealEstate realEstate){
        this.setId(realEstate.getId());
        this.setTitle(realEstate.getTitle());
        this.setDescription(realEstate.getDescription());
        this.setPrice(realEstate.getPrice());
        this.setSoldout(realEstate.isSoldout());
        this.setArea(realEstate.getArea());
        this.setAdministration_cost(realEstate.getAdministration_cost());
        this.setDirection(realEstate.getDirection());
        this.setLocation(realEstate.getLocation());
        this.setNumber_of_cars_parked(realEstate.getNumber_of_cars_parked());
        this.setRelay_object_type(realEstate.getRelay_object_type());
        this.setTransaction_type(realEstate.getTransaction_type());
        this.setResidence_availability_date(realEstate.getResidence_availability_date());
        this.setAdministrative_agency_approval_date(realEstate.getAdministrative_agency_approval_date());
        this.setAdministration_cost2(realEstate.getAdministration_cost2());
        this.setLatitude(realEstate.getLatitude());
        this.setLongitude(realEstate.getLongitude());
    }
}
