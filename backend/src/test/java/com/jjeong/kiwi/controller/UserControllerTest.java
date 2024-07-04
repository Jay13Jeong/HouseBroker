package com.jjeong.kiwi.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjeong.kiwi.KiwiApplication;
import com.jjeong.kiwi.config.TestConfig;
import com.jjeong.kiwi.dto.UserDto;
import com.jjeong.kiwi.service.AuthService;
import com.jjeong.kiwi.tool.DeepCopyViaSerialization;
import com.jjeong.kiwi.tool.GenerateJWT;
import com.jjeong.kiwi.tool.JsonConverter;
import com.jjeong.kiwi.tool.SampleData;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.jjeong.kiwi.model.User;

@SpringBootTest(classes = {KiwiApplication.class, TestConfiguration.class, TestConfig.class})
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private GenerateJWT generateJWT;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Order(0)
    void signUp_400_authentication_test() throws Exception {
        //** todo : when failing to authentication via email code
        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Order(0)
    void signUp_400_alreadyUser_test() throws Exception {
        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Order(0)
    void signUp_400_createFail_test() throws Exception {
        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Order(0)
    void getUserInfo_401_test() throws Exception {
        mockMvc.perform(get("/users/")
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(0)
    void getUserInfo_OK_test() throws Exception {
        User user = DeepCopyViaSerialization.deepCopy(SampleData.getUser());
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setDormant(user.isDormant());
//        appendsHATEOAS(userDto, userDto.getId());
        String dtoJson = JsonConverter.convertLinks(objectMapper.writeValueAsString(userDto));
        mockMvc.perform(get("/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(generateJWT.generateJWT_forTest())
            )
            .andExpect(status().isOk())
//            .andExpect(content().contentType("application/hal+json"))
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json(dtoJson));
    }

    @Test
    @Order(0)
    void getUserPermit_401_test() throws Exception {
        mockMvc.perform(get("/users/permit")
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(0)
    void deleteUser_401_test() throws Exception {
        mockMvc.perform(delete("/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(0)
    void returningDormantUser_401_test() throws Exception {
        mockMvc.perform(patch("/users/dormant")
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized());
    }

}