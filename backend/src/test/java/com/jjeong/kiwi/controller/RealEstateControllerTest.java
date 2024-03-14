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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private Long realEstateInitId = 1L;
    private Long realEstateTargetId = realEstateInitId;
    private int tableDataSize = 2;

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
        RealEstateWithImgPathDto dto = new RealEstateWithImgPathDto(re);
        dto.setId(realEstateTargetId);
        appendsHATEOAS(dto, dto.getId());
        String dtoJson = JsonConverter.convertLinks(objectMapper.writeValueAsString(dto));
        mockMvc.perform(post("/real-estates/")
                    .cookie(generateJWT_forTest())
                    .params(SampleData.getParams())
            )
            .andExpect(status().isCreated())
            .andExpect(content().contentType("application/hal+json"))
            .andExpect(content().json(dtoJson));
    }

    @Test
    @Order(2)
    void createRealEstate_concurrency_test() throws Exception {
        int threadSize = 1000;
        ExecutorService es = Executors.newFixedThreadPool(threadSize);
        CountDownLatch latch = new CountDownLatch(1);
        Long i = realEstateTargetId + 1;
        Long additionalIdx = i;
        Set<Long> resFinishChk = ConcurrentHashMap.newKeySet();

        System.out.println("size: " + i + ", " + additionalIdx);
        for (; i < threadSize + additionalIdx; i++){
            Long currIdx = i;
            es.submit(() -> {
                try {
                    RealEstate re = SampleData.getRealEstate();
                    RealEstateWithImgPathDto dto = new RealEstateWithImgPathDto(re);
                    String currTitle = "estateTitle" + currIdx;
                    dto.setTitle(currTitle);
                    appendsHATEOAS(dto, dto.getId());
                    String dtoJson = JsonConverter.convertLinks(objectMapper.writeValueAsString(dto));
                    latch.await();
                    mockMvc.perform(post("/real-estates/")
                            .cookie(generateJWT_forTest())
                            .params(SampleData.getParams())
                        )
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType("application/hal+json"))
                        .andExpect(jsonPath("$.title").value(currTitle));
                } catch (Exception e){
                    System.out.println("errIdx:" + currIdx);
                    e.printStackTrace();
                } finally {
                    resFinishChk.add(currIdx);
                }
            });
        }
        latch.countDown();
        while (resFinishChk.size() < threadSize){
            Thread.sleep(100);
        }
        es.shutdown();
        assertEquals(threadSize, resFinishChk.size());
        System.out.println(resFinishChk.size());
    }

    @Test
    @Order(3)
    void getRealEstates_test() throws Exception {
        RealEstate re = SampleData.getRealEstate();
        RealEstateWithImgPathDto dto = new RealEstateWithImgPathDto(re);
        dto.setId(realEstateInitId);
        appendsHATEOAS(dto, dto.getId());
        List<RealEstateWithImgPathDto> dtoList = Arrays.asList(dto);
        String dtoListJson = objectMapper.writeValueAsString(dtoList);
        System.out.println(dtoListJson);
        ResultActions ra = mockMvc.perform(get("/real-estates/"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"));
//            .andExpect(content().);
    }

    @Test
    @Order(4)
    void getRealEstateDetailById_test() throws Exception {
        RealEstate re = SampleData.getRealEstate();
        RealEstateWithImgPathDto dto = new RealEstateWithImgPathDto(re);
        dto.setId(realEstateInitId);
        appendsHATEOAS(dto, dto.getId());
        String dtoJson = JsonConverter.convertLinks(objectMapper.writeValueAsString(dto));
        mockMvc.perform(get(
            "/real-estates/" + realEstateInitId + "/detail"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/hal+json"))
            .andExpect(content().json(dtoJson));
    }

    @Test
    @Order(5)
    void getRealEstateById_test() throws Exception {
        RealEstate re = SampleData.getRealEstate();
        RealEstateWithoutImgDto dto = new RealEstateWithoutImgDto(re);
        dto.setId(realEstateInitId);
        appendsHATEOAS(dto, dto.getId());
        System.out.println(objectMapper.writeValueAsString(dto));
        String dtoJson = JsonConverter.convertLinks(objectMapper.writeValueAsString(dto));
        mockMvc.perform(get(
                "/real-estates/" + realEstateInitId + "/detail"))
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
