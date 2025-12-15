package com.example.Iot_Project.service;

import com.example.Iot_Project.dto.request.DeviceRequest;
import com.example.Iot_Project.dto.response.DeviceResponse;
import com.example.Iot_Project.enity.Device;
import com.example.Iot_Project.enums.ErrorCode;
import com.example.Iot_Project.enums.Status;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.mapper.DeviceMapper;
import com.example.Iot_Project.repository.DeviceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DeviceService {
    DeviceRepository deviceRepository;
    DeviceMapper deviceMapper;

    public DeviceResponse create(DeviceRequest request){

        Device device = deviceMapper.toDevice(request);
        device.setStatus(Status.ACTIVE.name());

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



}
