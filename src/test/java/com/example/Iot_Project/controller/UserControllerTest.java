package com.example.Iot_Project.controller;

import com.example.Iot_Project.dto.request.UserCreationRequest;
import com.example.Iot_Project.dto.response.UserResponse;
import com.example.Iot_Project.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userCreationRequest = UserCreationRequest.builder()
                .username("but i love you so")
                .password("12345678")
                .phone("0332909954")
                .email("tranchaucbhk54@gmail.com")
                .build();

        userResponse = UserResponse.builder()
                .id("eyJhbGciOiJIUzUxMiJ")
                .username("but i love you s")
                .phone("0332909954")
                .email("tranchaucbhk54@gmail.com")
                .build();
    }

    @Test
    void create() throws Exception {

        String requestBody = objectMapper.writeValueAsString(userCreationRequest);

        Mockito.when(userService.create(Mockito.any(UserCreationRequest.class)))
                .thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000));
    }
}

