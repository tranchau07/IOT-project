package com.example.Iot_Project.controller;

import com.example.Iot_Project.dto.request.SensorDataRequest;
import com.example.Iot_Project.dto.response.ApiResponse;
import com.example.Iot_Project.dto.response.SensorDataResponse;
import com.example.Iot_Project.service.SensorDataService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/sensor-data")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SensorDataController {
    SensorDataService sensorDataService;

    @PostMapping
    ApiResponse<SensorDataResponse> create(@RequestBody SensorDataRequest request){
        return ApiResponse.<SensorDataResponse>builder()
                .result(sensorDataService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<SensorDataResponse>> getAll(){
        return ApiResponse.<List<SensorDataResponse>>builder()
                .result(sensorDataService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<SensorDataResponse> getById(@PathVariable("id") String id){
        return ApiResponse.<SensorDataResponse>builder()
                .result(sensorDataService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<SensorDataResponse> update(@RequestBody SensorDataRequest request,@PathVariable("id") String id){
        return ApiResponse.<SensorDataResponse>builder()
                .result(sensorDataService.update(request, id))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<?> delete(@PathVariable String id){
        sensorDataService.delete(id);
        return ApiResponse.builder()
                .result("This sensor data is deleted")
                .build();
    }

    @GetMapping("/{sensorId}/latest")
    public ApiResponse<List<SensorDataResponse>> getLatestData(
            @PathVariable String sensorId,
            @RequestParam(defaultValue = "10") int limit) {

        return ApiResponse.<List<SensorDataResponse>>builder()
                .result(sensorDataService.getLatestSensorData(sensorId, limit))
                .build();
    }

    @GetMapping("/{sensorId}/range")
    public ApiResponse<List<SensorDataResponse>> getDataByRange(
            @PathVariable String sensorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        return ApiResponse.<List<SensorDataResponse>>builder()
                .result(sensorDataService.getDataByRange(sensorId, from, to))
                .build();
    }
}
