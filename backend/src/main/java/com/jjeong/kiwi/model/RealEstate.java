package com.jjeong.kiwi.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "real_estate")
@Getter
@Setter
@RequiredArgsConstructor
public class RealEstate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToMany
    @JoinTable(
        name = "real_estate_image",
        joinColumns = @JoinColumn(name = "real_estate_id"),
        inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private Set<Image> images = new HashSet<>();

    private String title;

    @Column(length = 5000)
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

}
