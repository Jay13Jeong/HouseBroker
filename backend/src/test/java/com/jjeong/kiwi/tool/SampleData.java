package com.jjeong.kiwi.tool;

import com.jjeong.kiwi.model.RealEstate;
import com.jjeong.kiwi.model.User;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class SampleData {
    @Getter
    private static RealEstate realEstate = new RealEstate();
    static  {
        realEstate.setId(1L);
        realEstate.setArea(33L);
        realEstate.setDescription("it is a sample");
        realEstate.setDirection("S");
        realEstate.setAdministration_cost(12345L);
        realEstate.setAdministration_cost2(23456L);
        realEstate.setAdministrative_agency_approval_date(LocalDateTime.now().toString());
        realEstate.setLatitude(127.123);
        realEstate.setLongitude(36.123);
        realEstate.setTransaction_type("매매");
        realEstate.setTitle("sample apartment");
        realEstate.setSoldout(false);
        realEstate.setResidence_availability_date(LocalDateTime.now().toString());
        realEstate.setRelay_object_type("apartment");
        realEstate.setPrice(987654321L);
        realEstate.setNumber_of_cars_parked(2L);
        realEstate.setLocation("seoul city");
        realEstate.setImage("NO_IMG");
        realEstate.setImage2("NO_IMG");
        realEstate.setImage3("NO_IMG");
        realEstate.setImage4("NO_IMG");
        realEstate.setImage5("NO_IMG");
        realEstate.setImage6("NO_IMG");
        realEstate.setImage7("NO_IMG");
        realEstate.setImage8("NO_IMG");
        realEstate.setImage9("NO_IMG");
        realEstate.setImage10("NO_IMG");
    }

    @Getter
    private static MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
    static {
        params.add("title", realEstate.getTitle());
        params.add("description", realEstate.getDescription());
        params.add("price", String.valueOf(realEstate.getPrice()));
        params.add("soldout", String.valueOf(realEstate.isSoldout()));
        params.add("relay_object_type", realEstate.getRelay_object_type());
        params.add("location", realEstate.getLocation());
        params.add("area", String.valueOf(realEstate.getArea()));
        params.add("transaction_type", realEstate.getTransaction_type());
        params.add("residence_availability_date", realEstate.getResidence_availability_date());
        params.add("administrative_agency_approval_date",
            realEstate.getAdministrative_agency_approval_date());
        params.add("number_of_cars_parked", String.valueOf(realEstate.getNumber_of_cars_parked()));
        params.add("direction", realEstate.getDirection());
        params.add("administration_cost", String.valueOf(realEstate.getAdministration_cost()));
        params.add("administration_cost2", String.valueOf(realEstate.getAdministration_cost2()));
        params.add("latitude", String.valueOf(realEstate.getLatitude()));
        params.add("longitude", String.valueOf(realEstate.getLongitude()));

    }

    public static MultiValueMap<String,String> getParamsByRealEstate(RealEstate realEstate) {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("title", realEstate.getTitle());
        params.add("description", realEstate.getDescription());
        params.add("price", String.valueOf(realEstate.getPrice()));
        params.add("soldout", String.valueOf(realEstate.isSoldout()));
        params.add("relay_object_type", realEstate.getRelay_object_type());
        params.add("location", realEstate.getLocation());
        params.add("area", String.valueOf(realEstate.getArea()));
        params.add("transaction_type", realEstate.getTransaction_type());
        params.add("residence_availability_date", realEstate.getResidence_availability_date());
        params.add("administrative_agency_approval_date",
            realEstate.getAdministrative_agency_approval_date());
        params.add("number_of_cars_parked", String.valueOf(realEstate.getNumber_of_cars_parked()));
        params.add("direction", realEstate.getDirection());
        params.add("administration_cost", String.valueOf(realEstate.getAdministration_cost()));
        params.add("administration_cost2", String.valueOf(realEstate.getAdministration_cost2()));
        params.add("latitude", String.valueOf(realEstate.getLatitude()));
        params.add("longitude", String.valueOf(realEstate.getLongitude()));
        return params;
    }

    @Getter
    private static User user = new User();
    static {
        user.setId(1L);
        user.setUsername("jay");
        user.setEmail("jay13@mail.com");
        user.setDormant(false);
        user.setPermitLevel(10);
//        user.setChatRooms();
    }

}
