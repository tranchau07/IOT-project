package com.example.Iot_Project.mapper;

import com.example.Iot_Project.dto.request.PermissionRequest;
import com.example.Iot_Project.dto.request.RoleRequest;
import com.example.Iot_Project.dto.response.PermissionResponse;
import com.example.Iot_Project.dto.response.RoleResponse;
import com.example.Iot_Project.enity.Permission;
import com.example.Iot_Project.enity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

//    @Mapping(target = "permissions", ignore = true)
    RoleResponse toRoleResponse(Role role);

    List<RoleResponse> toRoleResponses(List<Role> roles);
}
