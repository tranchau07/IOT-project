package com.example.Iot_Project.mapper;

import com.example.Iot_Project.dto.request.UserCreationRequest;
import com.example.Iot_Project.dto.request.UserUpdateRequest;
import com.example.Iot_Project.dto.response.UserResponse;
import com.example.Iot_Project.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
    List<UserResponse> toUserResponses(List<User> users);
}
