package com.example.Iot_Project.mapper;

import com.example.Iot_Project.dto.request.UserCreationRequest;
import com.example.Iot_Project.dto.request.UserUpdateRequest;
import com.example.Iot_Project.dto.response.SensorDataResponse;
import com.example.Iot_Project.dto.response.UserResponse;
import com.example.Iot_Project.enity.SensorData;
import com.example.Iot_Project.enity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
    List<UserResponse> toUserResponses(List<User> users);
}
