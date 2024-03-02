package com.jjeong.kiwi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class RealEstateImgPathDto extends RepresentationModel<RealEstateImgPathDto> {
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

    public void calcImgSlotStates() {
        imageSlotState.clear();
        if (!image.isEmpty() && !image.equals("NO_IMG")) this.imageSlotState.add(1);
        if (!image2.isEmpty() && !image2.equals("NO_IMG")) this.imageSlotState.add(2);
        if (!image3.isEmpty() && !image3.equals("NO_IMG")) this.imageSlotState.add(3);
        if (!image4.isEmpty() && !image4.equals("NO_IMG")) this.imageSlotState.add(4);
        if (!image5.isEmpty() && !image5.equals("NO_IMG")) this.imageSlotState.add(5);
        if (!image6.isEmpty() && !image6.equals("NO_IMG")) this.imageSlotState.add(6);
        if (!image7.isEmpty() && !image7.equals("NO_IMG")) this.imageSlotState.add(7);
        if (!image8.isEmpty() && !image8.equals("NO_IMG")) this.imageSlotState.add(8);
        if (!image9.isEmpty() && !image9.equals("NO_IMG")) this.imageSlotState.add(9);
        if (!image10.isEmpty() && !image10.equals("NO_IMG")) this.imageSlotState.add(10);
    }
}
