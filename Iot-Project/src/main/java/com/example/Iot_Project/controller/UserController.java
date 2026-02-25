package com.example.Iot_Project.controller;

import com.example.Iot_Project.dto.request.UserCreationRequest;
import com.example.Iot_Project.dto.request.UserUpdateRequest;
import com.example.Iot_Project.dto.response.ApiResponse;
import com.example.Iot_Project.dto.response.UserResponse;
import com.example.Iot_Project.enity.User;
import com.example.Iot_Project.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> create(@RequestBody @Valid UserCreationRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.create(request))
                .build();
    }

    @GetMapping
    List<UserResponse> getAllUsers(){
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getById(@PathVariable("userId") String id){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getByID(id))
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> update(@RequestBody UserUpdateRequest request,@PathVariable("userId") String id){
        return ApiResponse.<UserResponse>builder()
                .result(userService.update(request, id))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteById(@PathVariable("userId") String id){
        userService.deleteById(id);
        return ApiResponse.<String>builder()
                .result("User with id:" + id + " is deleted")
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

}
