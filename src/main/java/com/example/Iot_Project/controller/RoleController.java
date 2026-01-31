package com.example.Iot_Project.controller;

import com.example.Iot_Project.dto.request.PermissionRequest;
import com.example.Iot_Project.dto.request.RoleRequest;
import com.example.Iot_Project.dto.response.ApiResponse;
import com.example.Iot_Project.dto.response.PermissionResponse;
import com.example.Iot_Project.dto.response.RoleResponse;
import com.example.Iot_Project.service.PermissionService;
import com.example.Iot_Project.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping("")
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getListPerMission(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getListRole())
                .build();
    }

    @DeleteMapping("/{name}")
    ApiResponse<?> delete(@PathVariable("name") String name){
        roleService.delete(name);
        return ApiResponse.builder()
                .build();
    }
}
