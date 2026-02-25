package com.example.Iot_Project.controller;

import com.example.Iot_Project.dto.request.PermissionRequest;
import com.example.Iot_Project.dto.response.ApiResponse;
import com.example.Iot_Project.dto.response.PermissionResponse;
import com.example.Iot_Project.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping("")
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request){
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getListPerMission(){
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getListPermission())
                .build();
    }

    @DeleteMapping("/{name}")
    ApiResponse<?> delete(@PathVariable("name") String name){
        permissionService.delete(name);
        return ApiResponse.builder()
                .build();
    }
}
