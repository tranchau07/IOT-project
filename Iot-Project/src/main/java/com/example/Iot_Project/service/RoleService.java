package com.example.Iot_Project.service;

import com.example.Iot_Project.dto.request.RoleRequest;
import com.example.Iot_Project.dto.response.RoleResponse;
import com.example.Iot_Project.enity.Role;
import com.example.Iot_Project.enums.ErrorCode;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.mapper.RoleMapper;
import com.example.Iot_Project.repository.PermissionRepository;
import com.example.Iot_Project.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request){
        if(roleRepository.existsById(request.getName()))
            throw new AppException(ErrorCode.ROLE_EXISTED);

        Role role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public List<RoleResponse> getListRole(){
        List<Role> roles = roleRepository.findAll();
        return roleMapper.toRoleResponses(roles);
    }

    public void delete(String name){
        if(roleRepository.existsById(name))
            throw new AppException(ErrorCode.ROLE_EXISTED);

        roleRepository.deleteById(name);
    }
}
