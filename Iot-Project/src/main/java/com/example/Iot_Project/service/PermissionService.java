package com.example.Iot_Project.service;

import com.example.Iot_Project.dto.request.PermissionRequest;
import com.example.Iot_Project.dto.response.PermissionResponse;
import com.example.Iot_Project.enity.Permission;
import com.example.Iot_Project.enums.ErrorCode;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.mapper.PermissionMapper;
import com.example.Iot_Project.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request){
        if(permissionRepository.existsById(request.getName()))
            throw new AppException(ErrorCode.USER_EXISTED);

        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse((permissionRepository.save(permission)));
    }

    public List<PermissionResponse> getListPermission(){
        return permissionMapper.toPermissionResponses(permissionRepository.findAll());
    }

    public void delete(String name){
        if(permissionRepository.existsById(name))
            throw new AppException(ErrorCode.USER_EXISTED);
        permissionRepository.deleteById(name);
    }
}
