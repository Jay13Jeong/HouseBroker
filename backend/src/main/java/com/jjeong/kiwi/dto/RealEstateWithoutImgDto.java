package com.jjeong.kiwi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jjeong.kiwi.model.RealEstate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@RequiredArgsConstructor
public class RealEstateWithoutImgDto extends RepresentationModel<RealEstateWithoutImgDto> {

    private Long id;
    private String title;
    private String description;
    private Long price;
    private boolean soldout; //판매여부
    private String relay_object_type; //중계대상물종류
    private String location; //소재지 (지번, 동, 호수)
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

    public RealEstateWithoutImgDto(RealEstate model){
        this.setId(model.getId());
        this.setArea(model.getArea());
        this.setDescription(model.getDescription());
        this.setDirection(model.getDirection());
        this.setAdministration_cost(model.getAdministration_cost());
        this.setAdministration_cost2(model.getAdministration_cost2());
        this.setLatitude(model.getLatitude());
        this.setSoldout(model.isSoldout());
        this.setPrice(model.getPrice());
        this.setLocation(model.getLocation());
        this.setLongitude(model.getLongitude());
        this.setTitle(model.getTitle());
        this.setAdministrative_agency_approval_date(model.getAdministrative_agency_approval_date());
        this.setNumber_of_cars_parked(model.getNumber_of_cars_parked());
        this.setRelay_object_type(model.getRelay_object_type());
        this.setResidence_availability_date(model.getResidence_availability_date());
        this.setTransaction_type(model.getTransaction_type());
    }
}
