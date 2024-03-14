package com.jjeong.kiwi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjeong.kiwi.KiwiApplication;
import com.jjeong.kiwi.config.TestConfig;
import com.jjeong.kiwi.dto.RealEstateWithImgPathDto;
import com.jjeong.kiwi.dto.RealEstateWithoutImgDto;
import com.jjeong.kiwi.model.RealEstate;
import com.jjeong.kiwi.service.AuthService;
import com.jjeong.kiwi.tool.JsonConverter;
import com.jjeong.kiwi.tool.SampleData;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.FileCopyUtils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {KiwiApplication.class, TestConfiguration.class, TestConfig.class})
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RealEstateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;
    ObjectMapper objectMapper = new ObjectMapper();
    private final Long defaultPageLimit = 40L;
    private Long realEstateTargetId = 1L;

    private Cookie generateJWT_forTest() {
        // JWT 생성 및 설정
        String token = authService.generateToken(SampleData.getUser());
        // JWT cookie
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); // 쿠키 만료시간
        jwtCookie.setSecure(true);
        jwtCookie.setHttpOnly(true);
        return jwtCookie;
    }

    @Test
    @Order(1)
    void createRealEstate_test() throws Exception {
        RealEstate re = SampleData.getRealEstate();
        mockMvc.perform(post("/real-estates/")
                    .cookie(generateJWT_forTest())
                    .params(SampleData.getParams())
            )
            .andExpect(status().isCreated())
            .andExpect(content().contentType("application/hal+json"))
            ;
    }

//    @Test
//    @Order(2)
//    void updateRealEstate_withImage_test() throws Exception {
//        // 리소스 폴더 내의 파일 경로
//        ClassPathResource resource = new ClassPathResource("photo_env.png");
//
//        // 파일을 읽어 byte 배열로 변환
//        byte[] content = FileCopyUtils.copyToByteArray(resource.getInputStream());
//
//        // MockMultipartFile 생성
//        MockMultipartFile multipartFile = new MockMultipartFile("image", "photo_env.png", "image/png", content);
//
//        RealEstate re = SampleData.getRealEstate();
//        mockMvc.perform(multipart("/real-estates/" + realEstateTargetId)
//                .file(multipartFile)
//                .cookie(generateJWT_forTest())
//                .params(SampleData.getParams())
//            )
//            .andExpect(status().is2xxSuccessful())
//            .andExpect(content().contentType("application/hal+json"));
//
//    }

    @Test
    @Order(3)
    void getRealEstates_test() throws Exception {
        RealEstate re = SampleData.getRealEstate();
        RealEstateWithImgPathDto dto = new RealEstateWithImgPathDto(re);
        dto.setId(1L);
        appendsHATEOAS(dto, dto.getId());
        List<RealEstateWithImgPathDto> dtoList = Arrays.asList(dto);
        String dtoListJson = objectMapper.writeValueAsString(dtoList);
        ResultActions ra = mockMvc.perform(get("/real-estates/"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json(dtoListJson));

//        String content = ra.andReturn().getResponse().getContentAsString();
//        String target = "id\":";
//        int idIdx = content.indexOf(target);
//        String idString = "";
//        if (idIdx != -1){
//            for (int i = idIdx + target.length(); i < content.length(); i ++){
//                char c = content.charAt(i);
//                if (c == ',') break;
//                idString += c;
//            }
//        }
//        if (!idString.isEmpty()) realEstateTargetId = Long.valueOf(idString);
    }

    @Test
    @Order(4)
    void getRealEstateDetailById_test() throws Exception {
        RealEstate re = SampleData.getRealEstate();
        RealEstateWithImgPathDto dto = new RealEstateWithImgPathDto(re);
        dto.setId(realEstateTargetId);
        appendsHATEOAS(dto, dto.getId());
        System.out.println(objectMapper.writeValueAsString(dto));
        String dtoJson = JsonConverter.convertLinks(objectMapper.writeValueAsString(dto));
        mockMvc.perform(get(
            "/real-estates/" + realEstateTargetId + "/detail"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/hal+json"))
            .andExpect(content().json(dtoJson));
    }

    @Test
    @Order(5)
    void getRealEstateById_test() throws Exception {
        RealEstate re = SampleData.getRealEstate();
        RealEstateWithoutImgDto dto = new RealEstateWithoutImgDto(re);
        dto.setId(realEstateTargetId);
        appendsHATEOAS(dto, dto.getId());
        System.out.println(objectMapper.writeValueAsString(dto));
        String dtoJson = JsonConverter.convertLinks(objectMapper.writeValueAsString(dto));
        mockMvc.perform(get(
                "/real-estates/" + realEstateTargetId + "/detail"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/hal+json"))
            .andExpect(content().json(dtoJson));
    }

    private void appendsHATEOAS(RepresentationModel inst, Long id) {
        inst.add(linkTo(methodOn(RealEstateController.class)
            .getRealEstateById(id)).withSelfRel());
        inst.add(linkTo(methodOn(RealEstateController.class)
            .getRealEstates()).withRel("collection"));
        inst.add(linkTo(methodOn(RealEstateController.class)
            .getRealEstatesByKeySet(id + 1)).withRel("key-set"));
    }
}
