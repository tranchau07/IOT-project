package com.example.Iot_Project.service;

import com.example.Iot_Project.dto.request.UserCreationRequest;
import com.example.Iot_Project.dto.request.UserUpdateRequest;
import com.example.Iot_Project.dto.response.UserResponse;
import com.example.Iot_Project.enity.User;
import com.example.Iot_Project.enums.ErrorCode;
import com.example.Iot_Project.enums.Role;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.mapper.UserMapper;
import com.example.Iot_Project.repository.RoleRepository;
import com.example.Iot_Project.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;


    public UserResponse create(UserCreationRequest request) {

        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
//        user.setRoles(roles);

        return userMapper.toUserResponse(userRepository.save(user));
    }
//    @PreAuthorize("hasAuthorized('AUTH_LOGIN')")
    public UserResponse getMyInfo(){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user =  userRepository.findByUsername(userName).orElseThrow(() -> new AppException(ErrorCode.USER_DID_NOT_EXIST));
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() {
        return userMapper.toUserResponses(userRepository.findAll());
    }

    @PostAuthorize("returnObject.username==authentication.name")
    public UserResponse getByID(String id) {
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User is not found")));
    }

    @PostAuthorize("returnObject.username==authentication.name")
    public UserResponse update(UserUpdateRequest request, String id) {
        User user = userRepository.findById(id).orElseThrow(()
                -> new AppException(ErrorCode.USER_DID_NOT_EXIST));

        userMapper.updateUser(user, request);
        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }
    @PostAuthorize("returnObject.username==authentication.name")
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }
}
