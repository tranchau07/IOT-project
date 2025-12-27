package com.example.Iot_Project.controller;

import com.example.Iot_Project.dto.request.AuthorizedUserIdRequest;
import com.example.Iot_Project.dto.request.CommandRequest;
import com.example.Iot_Project.dto.request.DeviceRequest;
import com.example.Iot_Project.dto.response.ApiResponse;
import com.example.Iot_Project.dto.response.DeviceResponse;
import com.example.Iot_Project.enums.Command;
import com.example.Iot_Project.service.DeviceService;
import com.example.Iot_Project.service.MqttMessageHandlerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeviceController {
    DeviceService deviceService;
    MqttMessageHandlerService mqttMessageHandlerService;

    @PostMapping
    ApiResponse<DeviceResponse> create(@RequestBody DeviceRequest request){
        return ApiResponse.<DeviceResponse>builder()
                .result(deviceService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<DeviceResponse>> getAll(){
        return ApiResponse.<List<DeviceResponse>>builder()
                .result(deviceService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<DeviceResponse> getById(@PathVariable("id") String id){
        return ApiResponse.<DeviceResponse>builder()
                .result(deviceService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<DeviceResponse> update(@RequestBody DeviceRequest request,@PathVariable("id") String id){
        return ApiResponse.<DeviceResponse>builder()
                .result(deviceService.update(request, id))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<?> delete(@PathVariable String id){
        deviceService.delete(id);
        return ApiResponse.builder()
                .result("This device is deleted")
                .build();
    }

    @GetMapping("/room/{roomId}")
    ApiResponse<List<DeviceResponse>> getDevicesByRoom(@PathVariable String roomId){
        return ApiResponse.<List<DeviceResponse>>builder()
                .result(deviceService.getDeviceByRoom(roomId))
                .build();
    }

    @PostMapping("/{deviceId}/on")
    ApiResponse<?> turnOnDevice(@PathVariable String deviceId,@RequestBody CommandRequest request){
        mqttMessageHandlerService.sendControlCommand(deviceId, request);
        return ApiResponse.builder()
                .build();
    }
    @PostMapping("/{deviceId}/off")
    ApiResponse<?> turnOffDevice(@PathVariable String deviceId,@RequestBody CommandRequest request){
        mqttMessageHandlerService.sendControlCommand(deviceId, request);
        return ApiResponse.builder()
                .build();
    }

    @PutMapping("/{deviceId}/authorize")
    ApiResponse<DeviceResponse> authorizeUser(@RequestBody AuthorizedUserIdRequest request,@PathVariable String deviceId){
        return ApiResponse.<DeviceResponse>builder()
                .result(deviceService.authorizeUser(deviceId, request))
                .build();
    }

    @PutMapping("/{deviceId}/revoke")
    ApiResponse<DeviceResponse> revokeUser(@RequestBody AuthorizedUserIdRequest request,@PathVariable String deviceId){
        return ApiResponse.<DeviceResponse>builder()
                .result(deviceService.revokeUser(deviceId, request))
                .build();
    }
}
