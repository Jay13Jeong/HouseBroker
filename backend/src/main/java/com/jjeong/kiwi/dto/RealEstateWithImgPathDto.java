package com.jjeong.kiwi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jjeong.kiwi.model.RealEstate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@RequiredArgsConstructor
public class RealEstateWithImgPathDto extends RepresentationModel<RealEstateWithImgPathDto> {
    private Long id;
    private String image;
    private String image2;
    private String image3;
    private String image4;
    private String image5;
    private String image6;
    private String image7;
    private String image8;
    private String image9;
    private String image10;

    private List<Integer> imageSlotState = new ArrayList<>();

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

    public RealEstateWithImgPathDto(RealEstate model){
        this.setId(model.getId());
        this.setArea(model.getArea());
        this.setDescription(model.getDescription());
        this.setDirection(model.getDirection());
        this.setAdministration_cost(model.getAdministration_cost());
        this.setAdministration_cost2(model.getAdministration_cost2());
        this.setImage(model.getImage());
        this.setImage2(model.getImage2());
        this.setImage3(model.getImage3());
        this.setImage4(model.getImage4());
        this.setImage5(model.getImage5());
        this.setImage6(model.getImage6());
        this.setImage7(model.getImage7());
        this.setImage8(model.getImage8());
        this.setImage9(model.getImage9());
        this.setImage10(model.getImage10());
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
        this.calcImgSlotStates();
    }

    public void calcImgSlotStates() {
        this.imageSlotState.clear();
        if (!this.image.isEmpty() && !image.equals("NO_IMG")) this.imageSlotState.add(1);
        if (!this.image2.isEmpty() && !image2.equals("NO_IMG")) this.imageSlotState.add(2);
        if (!this.image3.isEmpty() && !image3.equals("NO_IMG")) this.imageSlotState.add(3);
        if (!this.image4.isEmpty() && !image4.equals("NO_IMG")) this.imageSlotState.add(4);
        if (!this.image5.isEmpty() && !image5.equals("NO_IMG")) this.imageSlotState.add(5);
        if (!this.image6.isEmpty() && !image6.equals("NO_IMG")) this.imageSlotState.add(6);
        if (!this.image7.isEmpty() && !image7.equals("NO_IMG")) this.imageSlotState.add(7);
        if (!this.image8.isEmpty() && !image8.equals("NO_IMG")) this.imageSlotState.add(8);
        if (!this.image9.isEmpty() && !image9.equals("NO_IMG")) this.imageSlotState.add(9);
        if (!this.image10.isEmpty() && !image10.equals("NO_IMG")) this.imageSlotState.add(10);
    }

    @JsonIgnore
    @Override
    public Links getLinks() {
        return super.getLinks();
    }
}
