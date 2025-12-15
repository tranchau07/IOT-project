package com.example.Iot_Project.mapper;

import com.example.Iot_Project.dto.request.DeviceRequest;
import com.example.Iot_Project.dto.response.DeviceResponse;
import com.example.Iot_Project.enity.Device;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeviceMapper {
//    User toUser(UserCreationRequest request);
//    UserResponse toUserResponse(User user);
//    void updateUser(@MappingTarget User user, UserUpdateRequest request);
    Device toDevice(DeviceRequest request);
    DeviceResponse toDeviceResponse(Device device);
    List<DeviceResponse> toDeviceResponses(List<Device> devices);
    void updateDevice(@MappingTarget Device device, DeviceRequest request);
}
