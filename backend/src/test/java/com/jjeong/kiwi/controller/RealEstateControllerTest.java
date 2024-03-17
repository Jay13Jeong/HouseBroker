package com.jjeong.kiwi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjeong.kiwi.KiwiApplication;
import com.jjeong.kiwi.config.TestConfig;
import com.jjeong.kiwi.dto.RealEstateWithImgPathDto;
import com.jjeong.kiwi.dto.RealEstateWithoutImgDto;
import com.jjeong.kiwi.model.RealEstate;
import com.jjeong.kiwi.service.AuthService;
import com.jjeong.kiwi.tool.DeepCopyViaSerialization;
import com.jjeong.kiwi.tool.JsonConverter;
import com.jjeong.kiwi.tool.SampleData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.greaterThan;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    private int threadSize = 500;
    private final String updateTitleName = "test-title-of-updateRealEstate-test";

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
    @Order(0)
    void createRealEstate_401_test() throws Exception {
        mockMvc.perform(post("/real-estates/")
                .contentType(MediaType.APPLICATION_JSON)
                .params(SampleData.getParams())
            )
            .andExpect(status().isUnauthorized());
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
                .contentType(MediaType.APPLICATION_JSON)
                .params(SampleData.getParams())
            )
            .andExpect(status().isCreated())
            .andExpect(content().contentType("application/hal+json"))
            .andExpect(content().json(dtoJson));
    }

    @Test
    @Order(2)
    void createRealEstate_concurrency_test() throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(threadSize);
        CountDownLatch latch = new CountDownLatch(1);
        Long i = realEstateTargetId + 1;
        Long additionalIdx = i;
        List<Future<?>> futures = new ArrayList<>();

        for (; i < threadSize + additionalIdx; i++){
            Long currIdx = i;
            futures.add(es.submit(() -> {
                try {
                    RealEstate re = DeepCopyViaSerialization.deepCopy(SampleData.getRealEstate());
                    String currTitle = "estateTitle" + currIdx;
                    re.setTitle(currTitle);
                    latch.await();
                    mockMvc.perform(post("/real-estates/")
                            .cookie(generateJWT_forTest())
                            .contentType(MediaType.APPLICATION_JSON)
                            .params(SampleData.getParamsByRealEstate(re))
                        )
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType("application/hal+json"))
                        .andExpect(jsonPath("$.title").value(currTitle));
                } catch (Exception e){
                    e.printStackTrace();
                    throw new AssertionError("Test failed at index: " + currIdx, e);
                }
            }));
        }
        latch.countDown();
        es.shutdown();
        es.awaitTermination(2, TimeUnit.SECONDS);
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.getCause().printStackTrace();
                fail();
            }
        }
    }

    @Test
    @Order(0)
    void getRealEstates_zeroLength_test() throws Exception {
        List<RealEstateWithImgPathDto> dtoList = Collections.emptyList();
        String dtoListJson = objectMapper.writeValueAsString(dtoList);
        mockMvc.perform(get("/real-estates/"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json(dtoListJson))
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Order(3)
    void getRealEstates_test() throws Exception {
        mockMvc.perform(get("/real-estates/"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(defaultPageLimit));
    }

    @Test
    @Order(0)
    void getRealEstatesByKeySet_zeroLength_test() throws Exception {
        Long nextId = threadSize - defaultPageLimit;
        mockMvc.perform(get("/real-estates/key-set/" + nextId))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Order(3)
    void getRealEstatesByKeySet_test() throws Exception {
        Long nextId = threadSize - defaultPageLimit;
        mockMvc.perform(get("/real-estates/key-set/" + nextId))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(defaultPageLimit));
    }

    @Test
    @Order(0)
    void getRealEstateDetailById_404_test() throws Exception {
        mockMvc.perform(get(
                "/real-estates/" + realEstateInitId + "/detail"))
            .andExpect(status().isNotFound());
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
    @Order(0)
    void getRealEstateById_notFound_test() throws Exception {
        Long targetId = realEstateTargetId;
        mockMvc.perform(get(
                "/real-estates/" + targetId))
            .andExpect(status().isNotFound());
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
                "/real-estates/" + realEstateInitId))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/hal+json"))
            .andExpect(content().json(dtoJson));
    }

    @Test
    @Order(0)
    @Transactional
    void updateRealEstate_401_test() throws Exception {
        RealEstate re = DeepCopyViaSerialization.deepCopy(SampleData.getRealEstate());
        re.setTitle(updateTitleName);
        mockMvc.perform(patch("/real-estates/" + realEstateTargetId)
                .contentType(MediaType.APPLICATION_JSON)
                .params(SampleData.getParamsByRealEstate(re))
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(0)
    @Transactional
    void updateRealEstate_404_test() throws Exception {
        RealEstate re = DeepCopyViaSerialization.deepCopy(SampleData.getRealEstate());
        re.setTitle(updateTitleName);
        mockMvc.perform(patch("/real-estates/" + realEstateTargetId)
                .cookie(generateJWT_forTest())
                .contentType(MediaType.APPLICATION_JSON)
                .params(SampleData.getParamsByRealEstate(re))
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    @Transactional
    void updateRealEstate_test() throws Exception {
        RealEstate re = DeepCopyViaSerialization.deepCopy(SampleData.getRealEstate());
        re.setTitle(updateTitleName);
        RealEstateWithImgPathDto dto = new RealEstateWithImgPathDto(re);
        dto.setId(realEstateInitId);
        appendsHATEOAS(dto, dto.getId());
        String dtoJson = JsonConverter.convertLinks(objectMapper.writeValueAsString(dto));
        mockMvc.perform(patch("/real-estates/" + realEstateTargetId)
                .cookie(generateJWT_forTest())
                .contentType(MediaType.APPLICATION_JSON)
                .params(SampleData.getParamsByRealEstate(re))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/hal+json"))
            .andExpect(content().json(dtoJson));
    }

    @Test
    @Order(7)
    void updateRealEstate_concurrency_test() throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(threadSize);
        CountDownLatch latch = new CountDownLatch(1);
        Long i = realEstateTargetId + 1;
        Long additionalIdx = i;
        List<Future<?>> futures = new ArrayList<>();

        for (; i < threadSize + additionalIdx; i++){
            Long currIdx = i;
            futures.add(es.submit(() -> {
                try {
                    RealEstate re = DeepCopyViaSerialization.deepCopy(SampleData.getRealEstate());
                    String currTitle = updateTitleName + currIdx;
                    re.setTitle(currTitle);
                    latch.await();
                    mockMvc.perform(patch("/real-estates/" + currIdx)
                            .cookie(generateJWT_forTest())
                            .contentType(MediaType.APPLICATION_JSON)
                            .params(SampleData.getParamsByRealEstate(re))
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/hal+json"))
                        .andExpect(jsonPath("$.title").value(currTitle));
                } catch (Exception e){
                    e.printStackTrace();
                    throw new AssertionError("Test failed at index: " + currIdx, e);
                }
            }));
        }
        latch.countDown();
        es.shutdown();
        es.awaitTermination(2, TimeUnit.SECONDS);
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.getCause().printStackTrace();
                fail();
            }
        }
        System.out.println(realEstateTargetId + 1 + ", " + additionalIdx);
    }

    @Test
    @Order(0)
    @Transactional
    void updateSequence_401_test() throws Exception {
        Long targetId = realEstateTargetId;
        mockMvc.perform(patch("/real-estates/" + targetId + "/sequence")
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(0)
    @Transactional
    void updateSequence_404_test() throws Exception {
        Long targetId = 1L;
        mockMvc.perform(patch("/real-estates/" + targetId + "/sequence")
                .cookie(generateJWT_forTest())
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    @Transactional
    void updateSequence_test() throws Exception {
        Long targetId = realEstateTargetId;
        mockMvc.perform(patch("/real-estates/" + targetId + "/sequence")
                .cookie(generateJWT_forTest())
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/hal+json"))
            .andExpect(jsonPath("$.id").value(greaterThan(targetId.intValue())))
            .andExpect(jsonPath("$.title")
                .value(SampleData.getRealEstate().getTitle()));
    }

    @Test
    @Order(0)
    @Transactional
    void updateRealEstateIsSoldOut_401_test() throws Exception {
        Long targetId = realEstateTargetId;
        Map<String, Boolean> requestBody = new HashMap<>();
        requestBody.put("soldout", true);
        mockMvc.perform(patch("/real-estates/" + targetId + "/sold-out")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(0)
    @Transactional
    void updateRealEstateIsSoldOut_404_test() throws Exception {
        Long targetId = realEstateTargetId;
        Map<String, Boolean> requestBody = new HashMap<>();
        requestBody.put("soldout", true);
        mockMvc.perform(patch("/real-estates/" + targetId + "/sold-out")
                .cookie(generateJWT_forTest())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    void updateRealEstateIsSoldOut_toTrue_test() throws Exception {
        Long targetId = realEstateTargetId;
        RealEstate re = SampleData.getRealEstate();
        RealEstateWithoutImgDto dto = new RealEstateWithoutImgDto(re);
        dto.setSoldout(true);
        appendsHATEOAS(dto, dto.getId());
        String dtoJson = JsonConverter.convertLinks(objectMapper.writeValueAsString(dto));
        Map<String, Boolean> requestBody = new HashMap<>();
        requestBody.put("soldout", true);
        System.out.println(objectMapper.writeValueAsString(requestBody));
        mockMvc.perform(patch("/real-estates/" + targetId + "/sold-out")
                .cookie(generateJWT_forTest())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/hal+json"))
            .andExpect(content().json(dtoJson));
    }

    @Test
    @Order(10)
    void updateRealEstateIsSoldOut_toFalse_test() throws Exception {
        Long targetId = realEstateTargetId;
        RealEstate re = SampleData.getRealEstate();
        RealEstateWithoutImgDto dto = new RealEstateWithoutImgDto(re);
        dto.setSoldout(false);
        appendsHATEOAS(dto, dto.getId());
        String dtoJson = JsonConverter.convertLinks(objectMapper.writeValueAsString(dto));
        Map<String, Boolean> requestBody = new HashMap<>();
        requestBody.put("soldout", false);
        System.out.println(objectMapper.writeValueAsString(requestBody));
        mockMvc.perform(patch("/real-estates/" + targetId + "/sold-out")
                .cookie(generateJWT_forTest())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/hal+json"))
            .andExpect(content().json(dtoJson));
    }

    @Test
    @Order(0)
    @Transactional
    void deleteRealEstate_401_test() throws Exception {
        Long targetId = realEstateTargetId;
        mockMvc.perform(delete("/real-estates/" + targetId)
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(0)
    @Transactional
    void deleteRealEstate_404_test() throws Exception {
        Long targetId = realEstateTargetId;
        mockMvc.perform(delete("/real-estates/" + targetId)
                .cookie(generateJWT_forTest())
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(11)
    @Transactional
    void deleteRealEstate_test() throws Exception {
        Long targetId = realEstateTargetId;
        mockMvc.perform(delete("/real-estates/" + targetId)
                .cookie(generateJWT_forTest())
            )
            .andExpect(status().isNoContent());
        mockMvc.perform(get(
                "/real-estates/" + targetId))
            .andExpect(status().isNotFound());
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
