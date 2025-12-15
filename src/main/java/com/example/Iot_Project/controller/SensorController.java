package com.example.Iot_Project.controller;

import com.example.Iot_Project.dto.request.SensorRequest;
import com.example.Iot_Project.dto.response.ApiResponse;
import com.example.Iot_Project.dto.response.SensorResponse;
import com.example.Iot_Project.service.SensorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SensorController {
    SensorService sensorService;

    @PostMapping
    ApiResponse<SensorResponse> create(@RequestBody SensorRequest request){
        return ApiResponse.<SensorResponse>builder()
                .result(sensorService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<SensorResponse>> getAll(){
        return ApiResponse.<List<SensorResponse>>builder()
                .result(sensorService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<SensorResponse> getById(@PathVariable("id") String id){
        return ApiResponse.<SensorResponse>builder()
                .result(sensorService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<SensorResponse> update(@RequestBody SensorRequest request,@PathVariable("id") String id){
        return ApiResponse.<SensorResponse>builder()
                .result(sensorService.update(request, id))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<?> delete(@PathVariable String id){
        sensorService.delete(id);
        return ApiResponse.builder()
                .result("This sensor is deleted")
                .build();
    }
}
