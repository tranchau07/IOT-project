package com.example.Iot_Project.mapper;

import com.example.Iot_Project.dto.request.PermissionRequest;
import com.example.Iot_Project.dto.request.UserCreationRequest;
import com.example.Iot_Project.dto.request.UserUpdateRequest;
import com.example.Iot_Project.dto.response.PermissionResponse;
import com.example.Iot_Project.dto.response.UserResponse;
import com.example.Iot_Project.enity.Permission;
import com.example.Iot_Project.enity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
    void updatePermission(@MappingTarget Permission permission, PermissionRequest request);
    List<PermissionResponse> toPermissionResponses(List<Permission> permissions);
}
