package com.example.Iot_Project.service;

import com.example.Iot_Project.dto.request.AuthorizedUserIdRequest;
import com.example.Iot_Project.dto.request.DeviceRequest;
import com.example.Iot_Project.dto.response.DeviceResponse;
import com.example.Iot_Project.enity.Device;
import com.example.Iot_Project.enity.User;
import com.example.Iot_Project.enums.ErrorCode;
import com.example.Iot_Project.enums.DeviceStatus;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.mapper.DeviceMapper;
import com.example.Iot_Project.repository.DeviceRepository;
import com.example.Iot_Project.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DeviceService {
    DeviceRepository deviceRepository;
    DeviceMapper deviceMapper;
    UserRepository userRepository;

    public DeviceResponse create(DeviceRequest request){

        var context = SecurityContextHolder.getContext().getAuthentication();
        String userName = context.getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_DID_NOT_EXIST));

        Device device = deviceMapper.toDevice(request);
        device.setStatus(DeviceStatus.ACTIVE.name());
        device.setOwnerId(user.getId());

        return deviceMapper.toDeviceResponse(deviceRepository.save(device));
    }

    public List<DeviceResponse> getAll(){
        List<Device> devices = deviceRepository.findAll();
        return deviceMapper.toDeviceResponses(devices);
    }

    public DeviceResponse getById(String id){
        Device device = deviceRepository.findById(id).orElseThrow(()
                -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        return deviceMapper.toDeviceResponse(device);
    }

    public DeviceResponse update(DeviceRequest request,String id){
        Device device = deviceRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        deviceMapper.updateDevice(device, request);

        return deviceMapper.toDeviceResponse(deviceRepository.save(device));
    }

    public void delete(String id){
        deviceRepository.deleteById(id);
    }

    public List<DeviceResponse> getDeviceByRoom(String roomId){
        List<Device> devices = deviceRepository.findByRoomId(roomId);
        return deviceMapper.toDeviceResponses(devices);
    }

    public DeviceResponse authorizeUser(String deviceId, AuthorizedUserIdRequest request){
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new AppException(ErrorCode.DEVICE_NOT_EXISTED));

        var context = SecurityContextHolder.getContext().getAuthentication();
        if(!device.getOwnerId().equals(context.getName()))
            throw new AppException(ErrorCode.DEVICE_NOT_EXISTED);

        List<String> authorizedUsers = device.getAuthorizedUserIds();
        String authorizeUserId = request.getId();
        if(!authorizedUsers.contains(authorizeUserId)){
            authorizedUsers.add(authorizeUserId);
            device.setAuthorizedUserIds(authorizedUsers);
        }

        return deviceMapper.toDeviceResponse(deviceRepository.save(device));
    }

    public DeviceResponse revokeUser(String deviceId, AuthorizedUserIdRequest request){
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new AppException(ErrorCode.DEVICE_NOT_EXISTED));

        var context = SecurityContextHolder.getContext().getAuthentication();
        if(!device.getOwnerId().equals(context.getName()))
            throw new AppException(ErrorCode.DEVICE_NOT_EXISTED);

        List<String> authorizedUsers = device.getAuthorizedUserIds();
        String authorizeUserId = request.getId();
        if(authorizedUsers.contains(authorizeUserId)){
            authorizedUsers.remove(authorizeUserId);
            device.setAuthorizedUserIds(authorizedUsers);
        }

        return deviceMapper.toDeviceResponse(deviceRepository.save(device));
    }

}
